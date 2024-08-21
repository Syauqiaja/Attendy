package com.hashtest.attendy.presentation.main.attendance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.utils.showCustomSnackBar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.hashtest.attendy.core.Resources
import com.hashtest.attendy.domain.models.Attendance
import com.hashtest.attendy.domain.models.User

class AttendanceViewModel:ViewModel() {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentAttendance(): LiveData<Attendance?>{
        val result = MutableLiveData<Attendance?>()

        db.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { uSnap ->
                val user = uSnap.toObject<User>()
                if(user?.attendanceRef == null){
                    result.value = null
                }else{
                    db.collection("attendances")
                        .document(user.attendanceRef!!)
                        .get()
                        .addOnSuccessListener { attSnap ->
                            result.value = attSnap.toObject<Attendance>()
                        }
                }
            }

        return result
    }

    fun createAttendance(): LiveData<Resources<Boolean>>{
        val result = MutableLiveData<Resources<Boolean>>()
        result.value = Resources.Loading()

        db.collection("users")
            .document(auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { uSnapshot ->
                val user = uSnapshot.toObject<User>()
                if(user!!.attendanceRef == null){  //When user doesn't have any live attendance
                    val attendance = Attendance(
                        userRef = uSnapshot.id,
                        locationRef = user.locationRef!!,
                        checkInTime = System.currentTimeMillis()
                    )
                    db.collection("attendances")
                        .document().also {
                            user.attendanceRef = it.id //Save created attendance id to user
                        }.set(attendance)
                        .addOnSuccessListener {
                            db.collection("users")
                                .document(auth.currentUser!!.uid)
                                .set(user)
                                .addOnSuccessListener { result.value = Resources.Success(true) }
                                .addOnFailureListener { result.value = Resources.Error(it.message ?: "Failed to punch in") }
                        }.addOnFailureListener { result.value = Resources.Error(it.message ?: "Failed to punch in") }
                }else{ //User already had attendance
                    db.collection("attendances")
                        .document(user.attendanceRef!!)
                        .update("checkOutTime", System.currentTimeMillis())
                        .addOnSuccessListener {
                            db.collection("users")
                                .document(auth.currentUser!!.uid)
                                .update("attendanceRef", null)
                                .addOnSuccessListener { result.value = Resources.Success(true) }
                                .addOnFailureListener { result.value = Resources.Error(it.message ?: "Failed to punch out") }
                        }.addOnFailureListener { result.value = Resources.Error(it.message ?: "Failed to punch out") }
                }
            }.addOnFailureListener { result.value = Resources.Error(it.message ?: "Failed to punch out") }

        return result
    }
}