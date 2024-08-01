package io.github.vulka.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.vulka.core.api.Platform
import io.github.vulka.ui.R
import kotlinx.serialization.Serializable

@Serializable
class Attendance(
    val platform: Platform,
    val userId: String,
    val credentials: String
)

@Composable
fun AttendanceScreen(
    args: Attendance,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = stringResource(R.string.Attendance))
    }
}