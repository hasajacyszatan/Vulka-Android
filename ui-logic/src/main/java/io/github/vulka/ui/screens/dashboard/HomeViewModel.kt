package io.github.vulka.ui.screens.dashboard

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.medzik.android.utils.runOnIOThread
import io.github.vulka.business.crypto.decryptCredentials
import io.github.vulka.business.sync.sync
import io.github.vulka.core.api.types.Student
import io.github.vulka.database.Credentials
import io.github.vulka.database.Repository
import io.github.vulka.ui.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    val student = MutableStateFlow<Student?>(null)

    var credentials = mutableStateOf("")
    var dbCredentials: MutableState<Credentials?> = mutableStateOf(null)

    // TODO: rework refreshing system
    // If data was synchronized when open app
    var wasRefreshed by mutableStateOf(false)

    // If data was saved in DB and now can be safely loaded
    var refreshed by mutableStateOf(false)

    fun init(args: Home) {
        viewModelScope.launch {
            credentials.value = decryptCredentials(args.credentials)
            dbCredentials.value = repository.credentials.getById(UUID.fromString(args.userId))!!

            refreshed = !args.firstSync

            student.tryEmit(dbCredentials.value!!.student)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun syncUi(
        context: Context,
        args: Home,
        student: Student,
        snackBarState: SnackbarHostState,
        pullToRefreshState: PullToRefreshState,
        onError: (Exception) -> Unit = {}
    ) = runOnIOThread {
        // Sync database
        try {
            sync(
                context = context,
                platform = args.platform,
                userId = UUID.fromString(args.userId),
                credentials = credentials.value,
                student = student
            )
        } catch (e: Exception) {
            runOnIOThread {
                val snackBarResult = snackBarState.showSnackbar(
                    message = "${context.getText(R.string.Error)}: ${e.message}",
                    actionLabel = context.getText(R.string.Details).toString(),
                    duration = SnackbarDuration.Long
                )
                when (snackBarResult) {
                    SnackbarResult.ActionPerformed -> {
                        onError(e)
                    }
                    else -> {}
                }
            }
        }

        refreshed = true

        pullToRefreshState.endRefresh()
    }

    // TODO: migrate room to flow objects
    fun getAllStudents(): Flow<List<Credentials>> = flow {
        emit(repository.credentials.getAll())
    }
}