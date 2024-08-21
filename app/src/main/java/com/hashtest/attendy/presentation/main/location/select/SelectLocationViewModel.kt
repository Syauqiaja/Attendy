package com.hashtest.attendy.presentation.main.location.select

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.hashtest.attendy.domain.models.LocationPlace
import com.hashtest.attendy.presentation.adapters.CheckableLocationAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class SelectLocationViewModel:ViewModel() {
    private val db = Firebase.firestore

    val locations: LiveData<Map<LocationPlace, String>> get() = _locations
    private val _locations = MutableLiveData<Map<LocationPlace, String>>(mapOf())

    fun refreshLocations(){
        db.collection("locations")
            .get()
            .addOnSuccessListener { locationSnapshot ->
                val newItemList = mutableMapOf<LocationPlace, String>()
                for(location in locationSnapshot){
                    if(!location.getString("locationName").isNullOrEmpty()){
                        Timber.tag("SelectLocationViewModel").d(location.toObject<LocationPlace>().toString())
                        newItemList[location.toObject<LocationPlace>()] = location.id
                    }
                }
                _locations.value = newItemList
            }
    }
}