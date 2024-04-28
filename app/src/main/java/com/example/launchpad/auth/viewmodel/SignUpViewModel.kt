package com.example.launchpad.auth.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.launchpad.data.Company
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import java.util.Timer
import java.util.TimerTask

class SignUpViewModel(val app: Application) : AndroidViewModel(app) {
    private val auth = Firebase.auth

    private val _isVerified = MutableLiveData<Boolean>()
    val isVerified = _isVerified

    private val _isSignUpSuccess = MutableLiveData<Boolean>()
    val isSignUpSuccess = _isSignUpSuccess

    private val _errorResponseMsg = MutableLiveData<String>()
    val errorResponseMsg = _errorResponseMsg

    fun signUpWithEmail(email: String, password: String, company: Company = Company()) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isSignUpSuccess.value = task.isSuccessful
                    if (company != null) {
                        getPreferences()
                            .edit()
                            .putString("company", Gson().toJson(company))
                            .apply()
                        Log.d(
                            "Sign Up Enterprise",
                            "signUpWithEmail: ${getPreferences().getString("company", null)}"
                        )

                    }
                    sendEmailVerification()
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

    fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()?.addOnFailureListener {
            errorResponseMsg.value = it.message
        }
    }

    private fun getPreferences() = app.getSharedPreferences("company", Context.MODE_PRIVATE)

    fun cancel() = Unit
}