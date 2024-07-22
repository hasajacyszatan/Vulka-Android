package io.github.vulka.ui.screens.dashboard.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.medzik.android.compose.ui.IconBox
import io.github.vulka.core.api.types.Note
import io.github.vulka.ui.R
import io.github.vulka.ui.common.EmptyView
import io.github.vulka.ui.utils.formatByLocale
import kotlinx.serialization.Serializable

@Serializable
class Notes(
    val userId: String
)

@Composable
fun NotesScreen(
    args: Notes,
    viewModel: NotesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.refresh(args)
    }

    val notesState by viewModel.notes.collectAsStateWithLifecycle(emptyList())
    val notes = notesState.map { it.note }.sortedBy { it.date }.reversed()

    if (notes.isNotEmpty()) {
        LazyColumn {
            notes.forEach { note ->
                item {
                    NotesCard {
                        note.name?.let {
                            Text(
                                text = it,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = note.content)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                modifier = Modifier.alpha(0.7f),
                                text = note.date.formatByLocale(Locale.current)
                            )
                            Text(
                                modifier = Modifier.alpha(0.7f),
                                text = note.creator
                            )
                        }
                    }
                }
            }
        }
    } else {
        EmptyView(
            icon = Icons.Default.EmojiEvents,
            title = stringResource(R.string.More_NotesAndAchievements_Empty)
        )
    }
}

@Composable
fun NotesCard(
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
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