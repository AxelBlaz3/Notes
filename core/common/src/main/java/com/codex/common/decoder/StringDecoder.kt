package com.codex.common.decoder

interface StringDecoder {

    fun decodeString(encodedString: String): String
}