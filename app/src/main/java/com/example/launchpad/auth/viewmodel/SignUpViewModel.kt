package com.example.launchpad.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.Timer
import java.util.TimerTask

class SignUpViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified = _isVerified

    private val _isSignUpSuccess = MutableLiveData<Boolean>()
    val isSignUpSuccess = _isSignUpSuccess

    private val _errorResponseMsg = MutableLiveData<String>()
    val errorResponseMsg = _errorResponseMsg

    fun signUpWithEmail(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isSignUpSuccess.value = task.isSuccessful
                    auth.currentUser?.sendEmailVerification()
                }
            }
            .addOnFailureListener {
                _errorResponseMsg.value = it.message
                Log.d("Error", "signUpWithEmail: " + it.message)
            }
    }

     fun checkEmailVerificationInterval() {
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                Log.d("timer task", "run: $isVerified")
                auth.currentUser?.reload()
                _isVerified.postValue(auth.currentUser!!.isEmailVerified)
                if (auth.currentUser!!.isEmailVerified)
                    timer.cancel()
            }
        }

        if (auth.currentUser != null)
            timer.scheduleAtFixedRate(timerTask, 0, 3000)
    }
}