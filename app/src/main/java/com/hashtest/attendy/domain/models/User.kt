package com.hashtest.attendy.domain.models

data class User(
    val name: String = "",
    val locationRef: String? = null,
    var attendanceRef: String? = null
)