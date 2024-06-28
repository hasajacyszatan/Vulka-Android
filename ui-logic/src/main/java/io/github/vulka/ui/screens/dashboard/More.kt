package io.github.vulka.ui.screens.dashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.vulka.core.api.Platform
import io.github.vulka.ui.R
import io.github.vulka.ui.VulkaViewModel
import kotlinx.serialization.Serializable

@Serializable
class More(
    val platform: Platform,
    val userId: String,
    val credentials: String
)

@Composable
fun MoreScreen(
    args: More,
    viewModel: VulkaViewModel = hiltViewModel()
) {

    Text(text = stringResource(R.string.More))
}