package com.example.launchpad.profile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MyProfileViewModel : ViewModel() {
    private val userLD = MutableLiveData<User?>()
    private val auth = Firebase.auth


    fun getUserLD() = userLD
    fun getUser() = userLD.value


}