package com.hashtest.attendy.presentation.main.location.select

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.hashtest.attendy.domain.models.LocationPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class SelectLocationViewModel:ViewModel() {
    private val db = Firebase.firestore

    val locations: Flow<List<LocationPlace>> = flow{
        val itemList = arrayListOf<LocationPlace>()

    }
}