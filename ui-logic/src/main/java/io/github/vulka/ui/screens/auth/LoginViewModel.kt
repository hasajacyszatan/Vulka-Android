package io.github.vulka.ui.screens.auth

import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import io.github.vulka.business.crypto.serializeCredentials
import io.github.vulka.core.api.Platform
import io.github.vulka.impl.librus.LibrusLoginClient
import io.github.vulka.impl.librus.LibrusLoginData
import io.github.vulka.impl.vulcan.VulcanLoginClient
import io.github.vulka.impl.vulcan.VulcanLoginData
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    val vulcanSymbol = mutableStateOf("")
    val vulcanToken = mutableStateOf("")
    val vulcanPin = mutableStateOf("")

    val librusLogin = mutableStateOf("")
    val librusPassword = mutableStateOf("")

    suspend fun login(platform: Platform, navController: NavController) {
        val client = when (platform) {
            Platform.Vulcan -> VulcanLoginClient()
            Platform.Librus -> LibrusLoginClient()
        }
        val requestData = when (platform) {
            Platform.Vulcan -> {
                // For Vulcan we must create keystore first
                val keystore = HebeKeystore.create(
                    alias = HebeKeystore.generateKeystoreName(vulcanSymbol.value),
                    firebaseToken = "",
                    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL} (Vulka)")

                VulcanLoginData(
                    symbol = vulcanSymbol.value,
                    token = vulcanToken.value,
                    pin = vulcanPin.value,
                    keystore = keystore
                )
            }

            Platform.Librus -> {
                LibrusLoginData(
                    login = librusLogin.value,
                    password = librusPassword.value
                )
            }
        }

        // Credentials will be encrypted in ChooseStudents screen,
        // because Vulcan implementation must encrypt credentials for every student,
        // then can save it to Room database
        // Currently encrypts only one credential
        val response = client.login(requestData)

        val data = serializeCredentials(response)

        withContext(Dispatchers.Main) {
            navController.navigate(
                ChooseStudents(
                    platform = platform,
                    credentialsData = data
                )
            )
        }
    }
}