package com.example.launchpad.data.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserViewModel(val app: Application) : AndroidViewModel(app) {
    private val USERS = Firebase.firestore.collection("user")
    private val _userLD = MutableLiveData<User>()

    private val auth = Firebase.auth
    private var listener: ListenerRegistration? = null

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

    fun getAuth() = auth.currentUser!!.let {
        User(
            uid = it.uid,
            email = it.email ?: "",
            avatar = if (it.photoUrl != null) it.photoUrl.toString() else "",
            name = it.displayName ?: "User#${it.uid.take(8)}",
            provider = it.providerData.toString()
        )
    }
    fun isEnterprise() = _userLD.value!!.isEnterprise
    fun isCompanyRegistered() = isEnterprise() && _userLD.value?.company_id != ""
    fun init() = Unit
}