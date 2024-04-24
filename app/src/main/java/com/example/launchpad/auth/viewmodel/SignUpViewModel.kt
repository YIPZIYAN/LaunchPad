package com.example.launchpad.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SignUpViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _isSignUpSuccess = MutableLiveData<Boolean>()
    val isSignUpSuccess = _isSignUpSuccess

    private val _responseMsg = MutableLiveData<String>()
    val responseMsg = _responseMsg

    fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                        _responseMsg.value =
                            "Email verification sent to ${auth.currentUser!!.email}"
                    }?.addOnFailureListener {
                        _responseMsg.value = it.message
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Error", "signUpWithEmail: " + it.message)
            }
    }


}