package com.chac.data.album.embedding.model

internal class SimpleTokenizer {
    fun tokenize(
        text: String,
        maxLength: Int = MAX_TOKEN_LENGTH,
    ): LongArray {
        val tokens = LongArray(maxLength)
        tokens[0] = START_TOKEN_ID

        val normalized = text.trim().lowercase()
        val maxBodyLength = maxLength - 2
        val body = normalized.take(maxBodyLength)

        body.forEachIndexed { index, char ->
            // Temporary char-level fallback tokenizer.
            tokens[index + 1] = ((char.code % TOKEN_MOD) + 1).toLong()
        }

        val endIndex = (body.length + 1).coerceAtMost(maxLength - 1)
        tokens[endIndex] = END_TOKEN_ID
        return tokens
    }

    companion object {
        private const val START_TOKEN_ID = 49406L
        private const val END_TOKEN_ID = 49407L
        private const val TOKEN_MOD = 32000
        const val MAX_TOKEN_LENGTH = 77
    }
}
