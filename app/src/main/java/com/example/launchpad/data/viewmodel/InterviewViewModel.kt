package com.example.launchpad.data.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.Interview
import com.example.launchpad.data.JobApplication
import com.example.launchpad.data.Pdf
import com.example.launchpad.util.JobApplicationState
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import org.joda.time.DateTime

class InterviewViewModel : ViewModel() {
    private val INTERVIEW = Firebase.firestore.collection("job_interview")
    private val _interviewLD = MutableLiveData<List<Interview>>()
    val isSuccess = MutableLiveData<Boolean>()
    val response = MutableLiveData<String>()


    private var listener: ListenerRegistration? = null

    init {
        listener = INTERVIEW.addSnapshotListener { snap, _ ->
            _interviewLD.value = snap?.toObjects()
        }
    }

    override fun onCleared() {
        listener?.remove()
    }

    fun init() = Unit

    fun getInterviewLD() = _interviewLD

    fun getAll() = _interviewLD.value ?: emptyList()

    fun get(interviewId: String) = getAll().find { it.id == interviewId }

    suspend fun set(interview: Interview) {
        val ref: DocumentReference = if (interview.id.isEmpty()) {
            INTERVIEW.document()
        } else {
            INTERVIEW.document(interview.id)
        }
        ref.set(interview).addOnCompleteListener {
            isSuccess.value = it.isSuccessful
        }.await()
    }

    fun updateInterviewList() {
        _interviewLD.value = getAll()
    }
}

