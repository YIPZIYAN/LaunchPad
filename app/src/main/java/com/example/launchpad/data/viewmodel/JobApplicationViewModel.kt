package com.example.launchpad.data.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.Company
import com.example.launchpad.data.JobApplication
import com.google.firebase.Firebase
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class JobApplicationViewModel : ViewModel() {
    private val JOBAPP = Firebase.firestore.collection("job_application")
    private val _jobAppLD = MutableLiveData<List<Company>>()
    val isSuccess = MutableLiveData<Boolean>()


    private var listener: ListenerRegistration? = null

    init {
        listener = JOBAPP.addSnapshotListener { snap, _ ->
            _jobAppLD.value = snap?.toObjects()
        }
    }

    override fun onCleared() {
        listener?.remove()
    }

    fun init() = Unit

    fun getJobAppLD() = _jobAppLD

    fun getAll() = _jobAppLD.value ?: emptyList()

    fun get(jobAppID: String) = getAll().find { it.id == jobAppID }

    suspend fun set(jobApp: JobApplication) {
        JOBAPP.document().set(jobApp).addOnCompleteListener {
            isSuccess.value = it.isSuccessful
        }.await()
    }


}