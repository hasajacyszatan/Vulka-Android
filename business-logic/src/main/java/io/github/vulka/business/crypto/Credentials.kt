package io.github.vulka.business.crypto

import dev.medzik.android.crypto.KeyStore
import dev.medzik.android.crypto.KeyStoreAlias
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.impl.librus.LibrusLoginCredentials
import io.github.vulka.impl.vulcan.VulcanLoginCredentials
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CredentialsKeyStore : KeyStoreAlias {
    override val name: String = "credentials"
}

fun serializeCredentialsAndEncrypt(response: LoginCredentials): String {
    val json = serializeCredentials(response)

    val cipherEnc = KeyStore.initForEncryption(CredentialsKeyStore, false)
    val cipherData = KeyStore.encrypt(cipherEnc, json.toByteArray())

    return cipherData.initializationVector + cipherData.cipherText
}

// TODO: maybe move somewhere else
fun serializeCredentials(credentials: LoginCredentials): String {
    return when (credentials) {
        is LibrusLoginCredentials -> Json.encodeToString(credentials)
        is VulcanLoginCredentials -> Json.encodeToString(credentials)
        else -> throw IllegalStateException()
    }
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
