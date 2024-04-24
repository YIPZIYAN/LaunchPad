package com.example.launchpad.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SignUpViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val _isSignUpSuccess = MutableLiveData<Boolean>()
    val isSignUpSuccess = _isSignUpSuccess
    fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _isSignUpSuccess.value = task.isSuccessful
            }
    }


}