package io.github.vulka.database.datastore

import dev.medzik.android.datastore.DataStore
import kotlinx.serialization.Serializable

@Serializable
@DataStore("settings")
data class Settings(
    val autoSync: Boolean = true,
    val syncInterval: Int = 15,
)