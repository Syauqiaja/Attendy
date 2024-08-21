package com.hashtest.attendy.domain.models

data class Attendance(
    val locationRef: String = "",
    val userRef: String = "",
    val checkInTime: Long = 0L,
    val checkOutTime: Long? = null
)
