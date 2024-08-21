package com.hashtest.attendy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationPlace(
    val locationName: String = "",
    val locationAddress: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val openTime: Int = 0,
    val closeTime: Int = 0
):Parcelable
