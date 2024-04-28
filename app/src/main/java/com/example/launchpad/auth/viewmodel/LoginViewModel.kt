package com.example.launchpad.auth.viewmodel

import android.app.Application
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.android.awaitFrame
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
        Log.d("USER", " ${auth.currentUser}")
    }

    suspend fun init() = awaitFrame()

    fun isLoggedIn() = auth.currentUser != null

    fun isVerified() = auth.currentUser!!.isEmailVerified

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                _signInResult.value = task.isSuccessful
            }
    }

    fun firebaseAuthWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            _signInResult.value = it.isSuccessful
            _response.value = it.exception?.message
        }
    }


}