package io.github.vulka.ui.screens.dashboard.more

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vulka.database.Repository
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    fun getNotes(credentials: UUID) = repository.notes.getByCredentialsId(credentials)
}