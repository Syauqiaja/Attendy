package com.hashtest.attendy.presentation.auth.register

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aglotest.algolist.utils.showCustomSnackBar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.hashtest.attendy.core.Resources
import com.hashtest.attendy.presentation.main.MainActivity

class RegisterViewModel: ViewModel() {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore

    fun createAuthUser(name: String, email: String, password: String): LiveData<Resources<Boolean>> {
        val result = MutableLiveData<Resources<Boolean>>()
        result.value = Resources.Loading()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {registerResult -> //Register email and password
            if(registerResult.isSuccessful){
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                auth.currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task -> //Add display name
                        if(task.isSuccessful){
                            db.collection("users") //Add data to firestore
                                .document(registerResult.result.user!!.uid)
                                .set(
                                    hashMapOf(
                                        "name" to registerResult.result.user!!.displayName
                                    )
                                ).addOnCompleteListener { taskToDb ->
                                    if(taskToDb.isSuccessful){
                                        result.value = Resources.Success(true) //The only success state
                                    }else{
                                        result.value = Resources.Error(taskToDb.exception?.message ?: "Failed to register")
                                    }
                                }
                        }else{
                            registerResult.result.user!!.delete()
                            auth.signOut()
                            result.value = Resources.Error(task.exception?.message ?: "Failed to register")
                        }
                    }
            }else{
                result.value = Resources.Error(registerResult.exception?.message ?: "Failed to register")
            }
        }

        return result
    }
}