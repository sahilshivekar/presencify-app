package edu.watumull.presencify.feature.attendance.mark_attendance

import kotlin.experimental.xor
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object DynamicQRCipher {
    const val SECRET_KEY = "Presencify_Secret_Key_V1"


    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(input: String): String {
        val inputBytes = input.encodeToByteArray()
        val keyBytes = SECRET_KEY.encodeToByteArray()
        val encryptedBytes = ByteArray(inputBytes.size) { i ->
            inputBytes[i] xor keyBytes[i % keyBytes.size]
        }
        return Base64.encode(encryptedBytes)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(input: String): String {
        val encryptedBytes = Base64.Default.decode(input)
        val keyBytes = SECRET_KEY.encodeToByteArray()
        val decryptedBytes = ByteArray(encryptedBytes.size) { i ->
            encryptedBytes[i] xor keyBytes[i % keyBytes.size]
        }
        return decryptedBytes.decodeToString()
    }
}