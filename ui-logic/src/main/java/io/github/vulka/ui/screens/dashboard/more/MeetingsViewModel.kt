package io.github.vulka.ui.screens.dashboard.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.database.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MeetingsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val meetings = MutableStateFlow<List<io.github.vulka.database.entities.Meetings>>(emptyList())

    fun refresh(args: Meetings) {
        viewModelScope.launch {
            repository.meetings.getByCredentialsId(UUID.fromString(args.userId)).collect {
                meetings.value = it
            }
        }
    }
}