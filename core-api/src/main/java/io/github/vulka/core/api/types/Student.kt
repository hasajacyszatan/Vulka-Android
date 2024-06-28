package io.github.vulka.core.api.types

/**
 * Represent student account in e-journal
 */
data class Student(
    val fullName: String,
    val isParent: Boolean,
    val parent: Parent?,
    val classId: String?,
    val customData: String? = null
)

/**
 * Represent parent of student
 */
@JvmInline
value class Parent(
    val fullName: String
)
