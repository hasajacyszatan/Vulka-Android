package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.vulka.ui.R
import io.github.vulka.ui.common.EmptyView
import io.github.vulka.ui.utils.formatByLocale
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
class Meetings(
    val userId: String
)

@Composable
fun MeetingsScreen(
    args: Meetings,
    viewModel: MeetingsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.refresh(args)
    }

    val meetingsState by viewModel.meetings.collectAsStateWithLifecycle()
    val meetings = meetingsState.map { it.meeting }.sortedBy { it.dateTime }.reversed()

    if (meetings.isNotEmpty()) {
        LazyColumn {
            meetings.forEach { meeting ->
                item {
                    MeetingCard(
                        color = if (meeting.dateTime.isAfter(LocalDateTime.now()))
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceContainer,
                    ) {
                        meeting.place?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = meeting.topic)
                        meeting.agenda?.let {
                            Text(
                                modifier = Modifier.alpha(0.7f),
                                text = meeting.agenda!!,
                                fontSize = 14.sp
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                modifier = Modifier.alpha(0.7f),
                                text = meeting.dateTime.formatByLocale(Locale.current)
                            )
                        }
                    }
                }
            }
        }
    } else {
        EmptyView(
            icon = Icons.Default.Group,
            title = stringResource(R.string.More_Meetings_Empty)
        )
    }
}
@Composable
fun MeetingCard(
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = color
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp)
                    .padding(12.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    content()
                }
            }
        }
    }
}