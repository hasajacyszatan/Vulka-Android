package io.github.vulka.ui.screens.dashboard.more

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.vulka.database.datastore.Settings
import io.github.vulka.database.datastore.SettingsGenerated.readFromSettings
import io.github.vulka.database.datastore.SettingsGenerated.writeToSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context
) : ViewModel() {

    val settings = MutableStateFlow<Settings?>(null)

    init {
        viewModelScope.launch {
            context.readFromSettings().collect {
                settings.value = it
            }
        }
    }

    fun setSettings(s: Settings) {
        viewModelScope.launch {
            settings.value = s
            context.writeToSettings(s)
        }
    }
}