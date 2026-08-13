package edu.watumull.presencify.core.data.local


interface StringCipher {
    fun encrypt(input: String): String
    fun decrypt(input: String): String
}

object NoOpStringCipher : StringCipher {
    override fun encrypt(input: String): String = input
    override fun decrypt(input: String): String = input
}
