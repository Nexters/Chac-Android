package com.chac.data.album.embedding.vector

import kotlin.math.sqrt

internal object VectorUtils {
    fun calculateCosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        if (v1.isEmpty() || v2.isEmpty()) return 0f

        val size = minOf(v1.size, v2.size)
        var dot = 0.0
        var norm1 = 0.0
        var norm2 = 0.0

        for (index in 0 until size) {
            val a = v1[index].toDouble()
            val b = v2[index].toDouble()
            dot += a * b
            norm1 += a * a
            norm2 += b * b
        }

        if (norm1 == 0.0 || norm2 == 0.0) return 0f
        return (dot / (sqrt(norm1) * sqrt(norm2))).toFloat()
    }
}
