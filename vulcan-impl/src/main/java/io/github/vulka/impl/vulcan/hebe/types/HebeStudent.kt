package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeStudent(
    @SerializedName("ClassDisplay")
    val classDisplay: String,
    @SerializedName("Login")
    val login: HebeLogin,
    @SerializedName("TopLevelPartition")
    val symbol: String,
    @SerializedName("Partition")
    val symbolCode: String,
    @SerializedName("State")
    val state: Int,
    @SerializedName("Pupil")
    val pupil: HebePupil,
    @SerializedName("Unit")
    val unit: HebeSchoolUnit,
    @SerializedName("ConstituentUnit")
    val school: HebeSchool,
    @SerializedName("MessageBox")
    val messageBox: HebeMessageBox,
    @SerializedName("Periods")
    val periods: List<HebePeriod>,
    @SerializedName("Journal")
    val journal: HebeJournal,
    @SerializedName("Context")
    val context: String
)

data class HebeJournal(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("YearStart")
    val yearStart: HebeDate,
    @SerializedName("YearEnd")
    val yearEnd: HebeDate,
    @SerializedName("PupilNumber")
    val pupilNumber: Int
)

data class HebeLogin(
    @SerializedName("Value")
    val email: String,
    @SerializedName("DisplayName")
    val name: String,
    @SerializedName("LoginRole")
    val role: String
)

data class HebeSchoolUnit(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Symbol")
    val code: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Short")
    val shortName: String,
    @SerializedName("DisplayName")
    val displayName: String,
    @SerializedName("RestURL")
    val restUrl: String,
    @SerializedName("Address")
    val address: String? = null
)

data class HebePupil(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("LoginId")
    val loginId: Int,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("Surname")
    val lastName: String,
    @SerializedName("Sex")
    val gender: Boolean,
    @SerializedName("SecondName")
    val secondName: String? = null,
    @SerializedName("LoginValue")
    val loginValue: String? = null
)

data class HebeMessageBox(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("GlobalKey")
    val globalKey: String,
    @SerializedName("Name")
    val name: String
)

data class HebeSchool(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Short")
    val shortName: String,
    @SerializedName("Address")
    val address: String? = null
)

data class HebePeriod(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Level")
    val level: Int,
    @SerializedName("Number")
    val number: Int,
    @SerializedName("Current")
    val current: Boolean,
    @SerializedName("Last")
    val last: Boolean,
    @SerializedName("Start")
    val start: HebeDate,
    @SerializedName("End")
    val end: HebeDate
)

