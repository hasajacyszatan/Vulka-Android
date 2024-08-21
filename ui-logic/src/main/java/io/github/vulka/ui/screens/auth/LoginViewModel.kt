package io.github.vulka.ui.screens.auth

import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import io.github.vulka.business.crypto.serializeCredentials
import io.github.vulka.core.api.Platform
import io.github.vulka.impl.librus.LibrusLoginClient
import io.github.vulka.impl.librus.LibrusLoginData
import io.github.vulka.impl.vulcan.hebe.VulcanHebeLoginClient
import io.github.vulka.impl.vulcan.hebe.VulcanHebeLoginData
import io.github.vulka.impl.vulcan.hebe.login.HebeKeystore
import io.github.vulka.impl.vulcan.prometheus.VulcanPrometheusLoginClient
import io.github.vulka.impl.vulcan.prometheus.VulcanPrometheusLoginData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    val eduVulcanLogin = mutableStateOf("")
    val eduVulcanPassword = mutableStateOf("")
    val eduVulcanAccessToken = mutableStateOf("")

    val vulcanHebeSymbol = mutableStateOf("")
    val vulcanHebeToken = mutableStateOf("")
    val vulcanHebePin = mutableStateOf("")

    val librusLogin = mutableStateOf("")
    val librusPassword = mutableStateOf("")

    suspend fun login(platform: Platform, navController: NavController) {
        val client = when (platform) {
            Platform.VulcanHebe -> VulcanHebeLoginClient()
            Platform.VulcanPrometheus -> VulcanPrometheusLoginClient()
            Platform.Librus -> LibrusLoginClient()
        }
        val requestData = when (platform) {
            Platform.VulcanHebe -> {
                // For Vulcan we must create keystore first
                val keystore = HebeKeystore.create(
                    alias = HebeKeystore.generateKeystoreName(vulcanHebeSymbol.value),
                    firebaseToken = "",
                    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL} (Vulka)")

                VulcanHebeLoginData(
                    symbol = vulcanHebeSymbol.value,
                    token = vulcanHebeToken.value,
                    pin = vulcanHebePin.value,
                    keystore = keystore
                )
            }

            Platform.Librus -> {
                LibrusLoginData(
                    login = librusLogin.value,
                    password = librusPassword.value
                )
            }

            Platform.VulcanPrometheus -> {
                if (eduVulcanAccessToken.value.isNotEmpty()) {
                    VulcanPrometheusLoginData(
                        login = eduVulcanLogin.value,
                        password = eduVulcanPassword.value,
                        accessToken = eduVulcanAccessToken.value,
                        deviceModel = Build.MODEL
                    )
                } else {
                    VulcanPrometheusLoginData(
                        login = eduVulcanLogin.value,
                        password = eduVulcanPassword.value,
                        deviceModel = Build.MODEL
                    )
                }
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