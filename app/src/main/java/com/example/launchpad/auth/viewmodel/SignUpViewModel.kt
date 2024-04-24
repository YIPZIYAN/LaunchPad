package com.example.launchpad.auth.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.util.Timer
import java.util.TimerTask

class SignUpViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified = _isVerified

    init {
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

        // Schedule the timer task to run every 5 seconds (5000 milliseconds)
        timer.scheduleAtFixedRate(timerTask, 0, 5000)
    }

    private val _isSignUpSuccess = MutableLiveData<Boolean>()
    val isSignUpSuccess = _isSignUpSuccess

    private val _responseMsg = MutableLiveData<String>()
    val responseMsg = _responseMsg

    private val _errorResponseMsg = MutableLiveData<String>()
    val errorResponseMsg = _errorResponseMsg

    fun init() = Unit

    fun setIsVerified(boolean: Boolean) {
        _isVerified.value = boolean
    }

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

    fun getCurrentUser() = auth.currentUser?.let {
        User(
            email = it.email ?: ""
        )
    }
}