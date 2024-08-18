package io.github.vulka.core.api

class ApiException(
    override val message: String,
    val code: Int,
//    val details: String? = null
) : Exception()
