package com.example.launchpad.auth.viewmodel

import android.app.Application
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.launchpad.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class LoginViewModel(val app: Application) :
    AndroidViewModel(app) {



    fun init() = Unit

    fun getCurrentUser() {
        val currentUser = Firebase.auth.currentUser
        currentUser?.let {

        }
    }



    fun logout() {
        Firebase.auth.signOut()
    }
}