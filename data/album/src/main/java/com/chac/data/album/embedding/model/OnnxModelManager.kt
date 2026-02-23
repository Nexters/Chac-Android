package com.chac.data.album.embedding.model

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.FloatBuffer
import java.nio.LongBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
internal class OnnxModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val environment: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val tokenizer = SimpleTokenizer()
    private val random = Random(System.currentTimeMillis())

    private var imageSession: OrtSession? = null
    private var textSession: OrtSession? = null

    init {
        loadModelSessions()
    }

    suspend fun encodeImage(bitmap: Bitmap): FloatArray = withContext(Dispatchers.Default) {
        val session = imageSession
        if (session == null) {
            Timber.e("Image model session is not initialized. Returning dummy embedding.")
            return@withContext generateDummyEmbedding()
        }

        runCatching {
            runImageInference(session, bitmap)
        }.onFailure { throwable ->
            Timber.e(throwable, "Failed to run image embedding inference. Returning dummy embedding.")
        }.getOrElse {
            generateDummyEmbedding()
        }
    }

    suspend fun encodeText(text: String): FloatArray = withContext(Dispatchers.Default) {
        val session = textSession
        if (session == null) {
            Timber.e("Text model session is not initialized. Returning dummy embedding.")
            return@withContext generateDummyEmbedding()
        }

        runCatching {
            runTextInference(session, text)
        }.onFailure { throwable ->
            Timber.e(throwable, "Failed to run text embedding inference. Returning dummy embedding.")
        }.getOrElse {
            generateDummyEmbedding()
        }
    }

    private fun loadModelSessions() {
        imageSession = createSession(MODEL_IMAGE_ASSET)
        textSession = createSession(MODEL_TEXT_ASSET)
    }

    private fun createSession(assetFileName: String): OrtSession? = runCatching {
        val modelBytes = context.assets.open(assetFileName).use { input -> input.readBytes() }
        environment.createSession(modelBytes)
    }.onFailure { throwable ->
        Timber.e(throwable, "Failed to load ONNX model asset: %s", assetFileName)
    }.getOrNull()

    private fun runImageInference(
        session: OrtSession,
        bitmap: Bitmap,
    ): FloatArray {
        val inputName = session.inputNames.firstOrNull()
            ?: return generateDummyEmbedding()

        val inputTensor = createImageTensor(bitmap)
        return try {
            val result = session.run(mapOf(inputName to inputTensor))
            try {
                val outputValue = firstOutputValue(result)
                extractEmbedding(outputValue)
            } finally {
                result.close()
            }
        } finally {
            inputTensor.close()
        }
    }

    private fun runTextInference(
        session: OrtSession,
        text: String,
    ): FloatArray {
        val inputNames = session.inputNames.toList()
        if (inputNames.isEmpty()) return generateDummyEmbedding()

        val inputIds = tokenizer.tokenize(text)
        val attentionMask = LongArray(inputIds.size) { index -> if (inputIds[index] == 0L) 0L else 1L }
        val tokenTypeIds = LongArray(inputIds.size) { 0L }

        val inputTensors = mutableMapOf<String, OnnxTensor>()
        return try {
            inputNames.forEach { inputName ->
                val lowerName = inputName.lowercase()
                val tensorSource = when {
                    lowerName.contains("input_ids") -> inputIds
                    lowerName.contains("attention") || lowerName.contains("mask") -> attentionMask
                    lowerName.contains("token_type") || lowerName.contains("segment") -> tokenTypeIds
                    else -> inputIds
                }
                inputTensors[inputName] = createLongTensor(tensorSource)
            }

            val result = session.run(inputTensors)
            try {
                val outputValue = firstOutputValue(result)
                extractEmbedding(outputValue)
            } finally {
                result.close()
            }
        } finally {
            inputTensors.values.forEach { tensor ->
                runCatching { tensor.close() }
            }
        }
    }

    private fun createImageTensor(bitmap: Bitmap): OnnxTensor {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)
        val channelSize = IMAGE_SIZE * IMAGE_SIZE
        val inputArray = FloatArray(IMAGE_CHANNELS * channelSize)

        for (y in 0 until IMAGE_SIZE) {
            for (x in 0 until IMAGE_SIZE) {
                val pixel = scaledBitmap.getPixel(x, y)

                val red = ((pixel shr 16 and 0xFF) / 255f - IMAGE_MEAN_R) / IMAGE_STD_R
                val green = ((pixel shr 8 and 0xFF) / 255f - IMAGE_MEAN_G) / IMAGE_STD_G
                val blue = ((pixel and 0xFF) / 255f - IMAGE_MEAN_B) / IMAGE_STD_B

                val index = y * IMAGE_SIZE + x
                inputArray[index] = red
                inputArray[channelSize + index] = green
                inputArray[channelSize * 2 + index] = blue
            }
        }

        if (scaledBitmap !== bitmap) {
            scaledBitmap.recycle()
        }

        return OnnxTensor.createTensor(
            environment,
            FloatBuffer.wrap(inputArray),
            longArrayOf(1, IMAGE_CHANNELS.toLong(), IMAGE_SIZE.toLong(), IMAGE_SIZE.toLong()),
        )
    }

    private fun createLongTensor(data: LongArray): OnnxTensor = OnnxTensor.createTensor(
        environment,
        LongBuffer.wrap(data),
        longArrayOf(1, data.size.toLong()),
    )

    private fun firstOutputValue(result: OrtSession.Result): Any? {
        val iterator = result.iterator()
        if (!iterator.hasNext()) return null
        return iterator.next().value.value
    }

    private fun extractEmbedding(rawOutput: Any?): FloatArray {
        val flattened = mutableListOf<Float>()
        flattenToFloat(rawOutput, flattened)
        if (flattened.isEmpty()) {
            Timber.e("ONNX output was empty. Returning dummy embedding.")
            return generateDummyEmbedding()
        }
        return flattened.toFloatArray()
    }

    private fun flattenToFloat(
        value: Any?,
        output: MutableList<Float>,
    ) {
        when (value) {
            null -> Unit
            is Number -> output.add(value.toFloat())
            is FloatArray -> value.forEach { number -> output.add(number) }
            is DoubleArray -> value.forEach { number -> output.add(number.toFloat()) }
            is IntArray -> value.forEach { number -> output.add(number.toFloat()) }
            is LongArray -> value.forEach { number -> output.add(number.toFloat()) }
            is Array<*> -> value.forEach { nested -> flattenToFloat(nested, output) }
            else -> Unit
        }
    }

    private fun generateDummyEmbedding(size: Int = DUMMY_VECTOR_SIZE): FloatArray =
        FloatArray(size) { random.nextFloat() }

    companion object {
        private const val MODEL_IMAGE_ASSET = "image_encoder.quant.onnx"
        private const val MODEL_TEXT_ASSET = "text_encoder.quant.onnx"

        private const val IMAGE_SIZE = 224
        private const val IMAGE_CHANNELS = 3

        private const val IMAGE_MEAN_R = 0.48145466f
        private const val IMAGE_MEAN_G = 0.4578275f
        private const val IMAGE_MEAN_B = 0.40821073f
        private const val IMAGE_STD_R = 0.26862954f
        private const val IMAGE_STD_G = 0.26130258f
        private const val IMAGE_STD_B = 0.27577711f

        private const val DUMMY_VECTOR_SIZE = 512
    }
}
