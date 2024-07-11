package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.vulka.ui.R
import kotlinx.serialization.Serializable

@Serializable
object Settings

@Composable
fun SettingsScreen() {
    Text(text = stringResource(R.string.More_Settings))
}