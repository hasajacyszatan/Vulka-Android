package io.github.vulka.business.crypto

import com.google.gson.Gson
import dev.medzik.android.crypto.KeyStore
import dev.medzik.android.crypto.KeyStoreAlias
import io.github.vulka.core.api.LoginCredentials

object CredentialsKeyStore : KeyStoreAlias {
    override val name: String = "credentials"
}

fun serializeCredentialsAndEncrypt(response: LoginCredentials): String {
    val json = Gson().toJson(response)

    val cipherEnc = KeyStore.initForEncryption(CredentialsKeyStore, false)
    val cipherData = KeyStore.encrypt(cipherEnc, json.toByteArray())

    return cipherData.initializationVector + cipherData.cipherText
}

// TODO: maybe move somewhere else
fun serializeCredentials(response: LoginCredentials): String {
    return Gson().toJson(response)
}

@OptIn(ExperimentalStdlibApi::class)
fun decryptCredentials(cipherData: String): String {
    // initialization vector length in hex string
    val ivLength = 12 * 2

    // extract IV and Cipher Text from hex string
    val iv = cipherData.substring(0, ivLength)
    val cipherText = cipherData.substring(ivLength)

    // decrypt cipher text
    val cipher = KeyStore.initForDecryption(CredentialsKeyStore, iv.hexToByteArray(), false)
    val decrypted = KeyStore.decrypt(cipher, cipherText)

    return String(decrypted)
}
