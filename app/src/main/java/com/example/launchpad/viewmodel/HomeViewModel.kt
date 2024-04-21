package com.example.launchpad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.launchpad.data.Job
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

class HomeViewModel(val app: Application) : AndroidViewModel(app) {
    private val JOBS = Firebase.firestore.collection("job")
    private val jobsLD = MutableLiveData<List<Job>>()
    private var listener: ListenerRegistration? = null

    init {
        listener = JOBS.addSnapshotListener{ snap, _ -> jobsLD.value = snap?.toObjects() }
    }

    override fun onCleared() {
        listener?.remove()
    }

    fun init() = Unit

    fun getJobsLD() = jobsLD

    fun getAll() = jobsLD.value

}