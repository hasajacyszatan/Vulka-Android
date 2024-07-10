package io.github.vulka.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import dev.medzik.android.compose.theme.DisabledAlpha
import dev.medzik.android.compose.theme.NormalAlpha
import dev.medzik.android.compose.theme.spacing
import dev.medzik.android.compose.ui.ButtonIconSpacer

@Composable
fun CardPreference(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    val alpha = if (enabled) NormalAlpha else DisabledAlpha

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.clickable(
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leading != null) {
                leading()

                ButtonIconSpacer()
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = MaterialTheme.spacing.extraSmall)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (trailing != null) {
                ButtonIconSpacer()

                trailing()
            }
        }
    }
}