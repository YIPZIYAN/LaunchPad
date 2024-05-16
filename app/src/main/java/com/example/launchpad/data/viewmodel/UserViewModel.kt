package com.example.launchpad.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(val app: Application) : AndroidViewModel(app) {
    private val USERS = Firebase.firestore.collection("user")
    private val _userLD = MutableLiveData<User>()

    private val auth = Firebase.auth
    private var listener: ListenerRegistration? = null

    val response = MutableLiveData<Boolean>()

    init {
        listener = auth.currentUser?.let {
            USERS.document(it.uid).addSnapshotListener { snap, _ ->
                _userLD.value = snap?.toObject()
            }
        }
    }

    fun getUserLD() = _userLD

    suspend fun set(user: User) {
        USERS.document(user.uid).set(user).addOnCompleteListener {
        }.await()
    }

    suspend fun update(user: User) {
        USERS.document(auth.currentUser!!.uid)
            .update(
                "name", user.name,
                "avatar", user.avatar
            )
            .addOnCompleteListener {
                response.value = it.isSuccessful
            }.await()
    }

    fun getAuth() = auth.currentUser!!.let {
        User(
            uid = it.uid,
            email = it.email ?: "",
            name = it.displayName ?: "User#${it.uid.take(8)}",
        )
    }

    fun isEnterprise() = _userLD.value!!.isEnterprise
    fun isCompanyRegistered() = _userLD.value?.company_id != ""

    fun isVerified() = auth.currentUser!!.isEmailVerified
    fun attachCompany(id: String) {
        USERS.document(getAuth().uid).update("company_id", id)
    }

    fun isGoogleLogin(): Boolean =
        auth.currentUser?.providerData?.any { it.providerId == "google.com" } ?: false


    fun init() = Unit
}