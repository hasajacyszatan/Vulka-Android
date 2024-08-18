package io.github.vulka.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.business.crypto.serializeCredentialsAndEncrypt
import io.github.vulka.core.api.LoginCredentials
import io.github.vulka.core.api.Platform
import io.github.vulka.core.api.UserClient
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Repository
import io.github.vulka.database.entities.Credentials
import io.github.vulka.impl.librus.LibrusLoginCredentials
import io.github.vulka.impl.librus.LibrusUserClient
import io.github.vulka.impl.vulcan.VulcanLoginCredentials
import io.github.vulka.impl.vulcan.VulcanUserClient
import io.github.vulka.ui.screens.dashboard.Home
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ChooseStudentsViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    var loaded by mutableStateOf(false)

    val students = mutableStateListOf<Student>()
    val selectedStudents = mutableStateListOf<Student>()

    private fun getCredentials(credentialsData: String, platform: Platform): LoginCredentials {
        return when (platform) {
            Platform.VulcanHebe -> Json.decodeFromString<VulcanLoginCredentials>(credentialsData)
            Platform.Librus -> Json.decodeFromString<LibrusLoginCredentials>(credentialsData)
        }
    }

    fun getClient(credentialsData: String,platform: Platform): UserClient {
        val credentials = getCredentials(credentialsData, platform)
        return when (platform) {
            Platform.VulcanHebe -> VulcanUserClient(credentials as VulcanLoginCredentials)
            Platform.Librus -> LibrusUserClient(credentials as LibrusLoginCredentials)
        }
    }

    fun refreshStudents(platform: Platform,client: UserClient) = viewModelScope.launch(Dispatchers.IO) {
        if (platform == Platform.Librus)
            (client as LibrusUserClient).renewCredentials()

        client.getStudents().forEach {
            students.add(it)
            selectedStudents.add(it)
        }

        loaded = true
    }

    suspend fun addStudents(
        platform: Platform,
        credentialsData: String,
        navController: NavController
    ) {
        var firstCredentials: Credentials? = null

        withContext(Dispatchers.IO) {
            for (student in selectedStudents) {
                val encryptedCredentials = Credentials(
                    platform = platform,
                    student = student,
                    data = serializeCredentialsAndEncrypt(getCredentials(credentialsData,platform))
                )

                if (firstCredentials == null)
                    firstCredentials = encryptedCredentials

                repository.credentials.insert(encryptedCredentials)
            }
        }

        withContext(Dispatchers.Main) {
            navController.navigate(
                Home(
                    userId = firstCredentials!!.id.toString(),
                    credentials = firstCredentials!!.data,
                    platform = platform,
                    firstSync = true
                )
            ) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = false
                    inclusive = true
                }
            }
        }
    }
}