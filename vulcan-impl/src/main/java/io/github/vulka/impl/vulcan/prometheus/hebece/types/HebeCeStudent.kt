package io.github.vulka.impl.vulcan.prometheus.hebece.types

import io.github.vulka.impl.vulcan.hebe.types.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeCeStudent(
    @SerialName("ClassDisplay")
    val classDisplay: String,
    @SerialName("Login")
    val login: HebeLogin? = null,
    @SerialName("TopLevelPartition")
    val symbol: String,
    @SerialName("Partition")
    val symbolCode: String,
    @SerialName("Capabilities")
    val capabilities: List<String>,
    @SerialName("State")
    val state: Int,
    @SerialName("Pupil")
    val pupil: HebePupil,
    @SerialName("Unit")
    val unit: HebeSchoolUnit,
    @SerialName("ConstituentUnit")
    val school: HebeSchool,
    @SerialName("MessageBox")
    val messageBox: HebeMessageBox,
    @SerialName("Periods")
    val periods: List<HebePeriod>,
    @SerialName("Journal")
    val journal: HebeJournal,
    @SerialName("Context")
    val context: String? = null
)