package io.github.vulka.impl.vulcan.hebe.types

import com.google.gson.annotations.SerializedName

data class HebeExam(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Key")
    val key: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("TypeId")
    val typeId: Int,
    @SerializedName("Content")
    val content: String,
    @SerializedName("DateCreated")
    val dateCreated: HebeDate,
    @SerializedName("DateModify")
    val dateModify: HebeDate,
    @SerializedName("Deadline")
    val deadline: HebeDate,
    @SerializedName("Creator")
    val creator: HebeTeacher,
    @SerializedName("Subject")
    val subject: HebeSubject,
    @SerializedName("Didactics")
    val didactics: Any?,
)