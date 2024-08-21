package com.hashtest.attendy.presentation.main.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aglotest.algolist.utils.minutesToTime
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.hashtest.attendy.domain.models.Attendance
import com.hashtest.attendy.domain.models.LocationPlace
import com.hashtest.attendy.domain.models.User
import timber.log.Timber

class HomeViewModel:ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    val userLocation : LiveData<LocationPlace?> get() = _userLocation
    private val _userLocation = MutableLiveData<LocationPlace?>()

    val attendances: LiveData<List<Attendance>> get() = _attendances
    private val _attendances = MutableLiveData<List<Attendance>>()

    fun getAllAttendances(){
        db.collection("attendances")
            .whereEqualTo("userRef", auth.currentUser!!.uid)
            .limit(5)
            .get()
            .addOnSuccessListener { querySnap ->
                val listAttendance = querySnap.documents.map {
                    it.toObject<Attendance>()!!
                }
                _attendances.value = listAttendance
            }.addOnFailureListener {
                _attendances.value = listOf()
            }
    }

    fun getUserLocation(){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                if(user?.locationRef != null){
                    db.collection("locations")
                        .document(user.locationRef)
                        .get()
                        .addOnSuccessListener { snap ->
                            snap.toObject<LocationPlace>()?.let { location ->
                                _userLocation.value = location
                            }
                        }.addOnFailureListener { e ->
                            Timber.tag("HomeViewModel").e(e)
                        }
                }else{
                    _userLocation.value = null
                }
            }.addOnFailureListener { e ->
                Timber.tag("HomeViewModel").e(e)
            }
    }
}