package com.example.launchpad.data.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.launchpad.data.Company
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await

class UserViewModel(val app: Application) : AndroidViewModel(app) {
    private val USERS = Firebase.firestore.collection("user")
    private val _usersLD = MutableLiveData<User>()

    private val auth = Firebase.auth
    private var listener: ListenerRegistration? = null

    init {
        auth.currentUser?.reload()
        auth.currentUser?.let {
            if (hasUser()) {
                USERS.document(it.uid).update(
                    "name", it.displayName ?: "User#${it.uid.take(8)}",
                    "avatar", if (it.photoUrl != null) it.photoUrl.toString() else "",
                )
            }
        }

        listener = auth.currentUser?.let {
            USERS.document(it.uid).addSnapshotListener { snap, _ ->
                _usersLD.value = snap?.toObject()

            }
        }
    }

    fun getUserLD() = _usersLD

    suspend fun set(user: User) {
        USERS.document(user.uid).set(user).addOnCompleteListener {
        }.await()
        Log.d("USER", "set: set user to firebase")
    }

    suspend fun setCompany(user: User) {
        Log.d("COMPANY", "set: set company to firebase")

        if (getPreferences().getString("company", null) != null) {
            USERS.document(user.uid).update("company", getCompanyFromLocal())
                .addOnCompleteListener {
                }.await()
        }
    }

    private fun hasUser() = USERS.document(auth.currentUser!!.uid).get().isSuccessful

    fun getAuth() = auth.currentUser!!.let {
        User(
            uid = it.uid,
            email = it.email ?: "",
            avatar = if (it.photoUrl != null) it.photoUrl.toString() else "",
            name = it.displayName ?: "User#${it.uid.take(8)}",
        )
    }

    private fun getCompanyFromLocal(): Company =
        Gson().fromJson(getPreferences().getString("company", null), Company::class.java)

    fun isEnterprise() = _usersLD.value!!.company != null
    private fun getPreferences() = app.getSharedPreferences("company", Context.MODE_PRIVATE)

    fun init() = Unit
}