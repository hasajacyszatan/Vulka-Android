package io.github.vulka.database.datastore

import dev.medzik.android.datastore.DataStore
import kotlinx.serialization.Serializable

@Serializable
@DataStore("last_sync")
data class LastSync(
    val lastSync: Long = 0,
)