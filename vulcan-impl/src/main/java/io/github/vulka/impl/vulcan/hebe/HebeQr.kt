package io.github.vulka.impl.vulcan.hebe

import android.annotation.SuppressLint
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

// Code from: https://github.com/wulkanowy/qr/tree/master/jvm

object HebeQr {
    const val HEBE_QR_DEFAULT_KEY = "tDVS4ykCBBAeN33h"

    @SuppressLint("GetInstance")
    private val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

    fun decode(password: String, content: String): String {
        val input = Base64.getDecoder().decode(content.toByteArray())

        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(password.toByteArray(), "AES"))

        val plainText = ByteArray(cipher.getOutputSize(input.size))
        var ptLength = cipher.update(input, 0, input.size, plainText, 0)

        ptLength += cipher.doFinal(plainText, ptLength)

        return String(plainText).substring(0, ptLength)
    }

    fun encode(password: String, content: String): String {
        val input = content.toByteArray()

        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(password.toByteArray(), "AES"))

        val cipherText = ByteArray(cipher.getOutputSize(input.size))
        var ctLength = cipher.update(input, 0, input.size, cipherText, 0)

        ctLength += cipher.doFinal(cipherText, ctLength)

        return Base64.getEncoder().encodeToString(cipherText)
    }
}