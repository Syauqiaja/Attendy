package com.hashtest.attendy.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationPlace(
    val locationName: String,
    val locationAddress: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
):Parcelable
