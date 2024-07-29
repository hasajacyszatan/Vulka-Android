package io.github.vulka.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.medzik.android.compose.theme.combineAlpha

@Composable
fun WeekDayHeading(
    weekday: String
) {
    Surface(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .padding(3.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer.combineAlpha(0.5f)
    ) {
        Text(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            text = weekday
        )
    }
}