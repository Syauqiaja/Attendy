package com.hashtest.attendy.domain.models

data class User(
    val name: String = "",
    val locationRef: String? = null,
    val attendances: List<Attendance>? = null
)