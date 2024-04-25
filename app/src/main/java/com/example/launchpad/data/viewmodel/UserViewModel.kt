package com.example.launchpad.data.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val USERS = Firebase.firestore.collection("user")
    private val _usersLD = MutableLiveData<User>()

    private val auth = Firebase.auth
    private var listener: ListenerRegistration? = null

    init {
        auth.currentUser?.reload()
        USERS.document(getAuth().uid).set(getAuth())

        listener = auth.currentUser?.let {
            USERS.document(it.uid).addSnapshotListener { snap, _ ->
                _usersLD.value = snap?.toObject()
            }
        }
    }

    fun getUserLD() = _usersLD

    suspend fun set(user: User) {
        Log.d("USER", "set: trying to set user...")

        if (!hasUser(user)) {
            USERS.document(user.uid).set(user).addOnCompleteListener {
            }.await()
            Log.d("USER", "set: set user to firebase")
        }
    }

    private fun hasUser(user: User) = USERS.document(user.uid).get().isSuccessful

    fun getAuth() = auth.currentUser!!.let {
        User(
            uid = it.uid,
            email = it.email ?: "",
            avatar = if (it.photoUrl != null) it.photoUrl.toString() else "",
            name = it.displayName ?: "User#${it.uid.take(8)}",
        )
    }

    fun init() = Unit
}