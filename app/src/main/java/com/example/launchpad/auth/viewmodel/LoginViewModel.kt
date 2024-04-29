package com.example.launchpad.auth.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class LoginViewModel(val app: Application) :
    AndroidViewModel(app) {

    private val auth = Firebase.auth

    private val _signInResult = MutableLiveData<Boolean>()
    val signInResult = _signInResult

    private val _response = MutableLiveData<String>()
    val response = _response

    init {
        auth.currentUser?.reload()
        if (!isLoggedIn()) auth.signOut()
        Log.d("USER", " ${auth.currentUser?.uid}")
    }

    fun isLoggedIn() = auth.currentUser != null

    fun isVerified() = auth.currentUser!!.isEmailVerified

    suspend fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _signInResult.value = task.isSuccessful
            }.await()
    }

    suspend fun firebaseAuthWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            _signInResult.value = it.isSuccessful
            _response.value = it.exception?.message
        }.await()
    }


}