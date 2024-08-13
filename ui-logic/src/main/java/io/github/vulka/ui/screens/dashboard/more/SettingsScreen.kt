package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.medzik.android.compose.theme.spacing
import dev.medzik.android.compose.ui.GroupTitle
import dev.medzik.android.compose.ui.preference.SwitcherPreference
import io.github.vulka.ui.R
import kotlinx.serialization.Serializable

@Serializable
object Settings

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    if (settings != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            GroupTitle(
                text = stringResource(R.string.Settings_Synchronization),
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.medium,
                    vertical = 3.dp
                )
            )

            SwitcherPreference(
                title = stringResource(R.string.Settings_AutoSync),
                checked = settings!!.autoSync,
                onCheckedChange = {
                    viewModel.setSettings(
                        settings!!.copy(
                            autoSync = it
                        )
                    )
                }
            )
        }
    }
}