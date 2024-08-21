package io.github.vulka.impl.librus.api.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Example success:
// {
//	"status": "ok",
//	"goTo": "\/OAuth\/Authorization\/2FA?client_id=46"
//}

// Example error:
// {
//	"status": "error",
//	"errors": [
//		{
//			"code": 0,
//			"message": "Nieprawidłowy login i\/lub hasło."
//		}
//	]
//}

@Serializable
internal data class LoginResponse(
    val status: LoginStatus,
    val goTo: String?,
    val errors: List<LoginError>?
)

@Serializable
internal enum class LoginStatus {
    @SerialName("ok")
    OK,
    @SerialName("error")
    ERROR
}

@Serializable
internal data class LoginError(
    val code: Int,
    val message: String
)
