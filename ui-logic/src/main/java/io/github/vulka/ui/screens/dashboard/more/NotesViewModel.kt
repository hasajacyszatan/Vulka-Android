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
class NotesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val notes = MutableStateFlow<List<io.github.vulka.database.entities.Notes>>(emptyList())

    fun refresh(args: Notes) {
        viewModelScope.launch {
            repository.notes.getByCredentialsId(UUID.fromString(args.userId)).collect {
                notes.value = it
            }
        }
    }
}