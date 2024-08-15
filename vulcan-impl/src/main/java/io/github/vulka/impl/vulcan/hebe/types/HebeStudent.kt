package io.github.vulka.impl.vulcan.hebe.types

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HebeStudent(
    @SerialName("ClassDisplay")
    val classDisplay: String,
    @SerialName("Login")
    val login: HebeLogin,
    @SerialName("TopLevelPartition")
    val symbol: String,
    @SerialName("Partition")
    val symbolCode: String,
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
    val context: String
)

@Serializable
data class HebeJournal(
    @SerialName("Id")
    val id: Int,
    @SerialName("YearStart")
    val yearStart: HebeDate,
    @SerialName("YearEnd")
    val yearEnd: HebeDate,
    @SerialName("PupilNumber")
    val pupilNumber: Int
)

@Serializable
data class HebeLogin(
    @SerialName("Value")
    val email: String,
    @SerialName("DisplayName")
    val name: String,
    @SerialName("LoginRole")
    val role: String
)

@Serializable
data class HebeSchoolUnit(
    @SerialName("Id")
    val id: Int,
    @SerialName("Symbol")
    val code: String,
    @SerialName("Name")
    val name: String,
    @SerialName("Short")
    val shortName: String,
    @SerialName("DisplayName")
    val displayName: String,
    @SerialName("RestURL")
    val restUrl: String,
    @SerialName("Address")
    val address: String? = null
)

@Serializable
data class HebePupil(
    @SerialName("Id")
    val id: Int,
    @SerialName("LoginId")
    val loginId: Int,
    @SerialName("FirstName")
    val firstName: String,
    @SerialName("Surname")
    val lastName: String,
    @SerialName("Sex")
    val gender: Boolean,
    @SerialName("SecondName")
    val secondName: String? = null,
    @SerialName("LoginValue")
    val loginValue: String? = null
)

@Serializable
data class HebeMessageBox(
    @SerialName("Id")
    val id: Int,
    @SerialName("GlobalKey")
    val globalKey: String,
    @SerialName("Name")
    val name: String
)

@Serializable
data class HebeSchool(
    @SerialName("Id")
    val id: Int,
    @SerialName("Name")
    val name: String,
    @SerialName("Short")
    val shortName: String,
    @SerialName("Address")
    val address: String? = null
)

@Serializable
data class HebePeriod(
    @SerialName("Id")
    val id: Int,
    @SerialName("Level")
    val level: Int,
    @SerialName("Number")
    val number: Int,
    @SerialName("Current")
    val current: Boolean,
    @SerialName("Last")
    val last: Boolean,
    @SerialName("Start")
    val start: HebeDate,
    @SerialName("End")
    val end: HebeDate
)
