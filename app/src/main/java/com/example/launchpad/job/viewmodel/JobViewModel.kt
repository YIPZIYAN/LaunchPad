package com.example.launchpad.job.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.Job
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.Firebase
import kotlinx.coroutines.tasks.await

class JobViewModel(val app: Application) : AndroidViewModel(app) {
    private val JOBS = Firebase.firestore.collection("job")
    private val jobsLD = MutableLiveData<List<Job>>()
    private var listener: ListenerRegistration? = null
    private val required = "* Required"

    init {
        listener = JOBS.addSnapshotListener { snap, _ ->
            jobsLD.value = snap?.toObjects()
            updateResult()
        }
    }

    override fun onCleared() {
        listener?.remove()
    }

    fun init() = Unit

    fun getJobsLD() = jobsLD

    fun getAll() = jobsLD.value ?: emptyList()

    fun get(jobID: String) = getAll().find { it.jobID == jobID }

    fun getJobLive(jobList: List<Job>, jobID: String) = jobList.find { it.jobID == jobID }!!

    fun set(job: Job) {
        JOBS.document().set(job)
    }

    suspend fun update(job: Job) {
        JOBS.document(job.jobID).set(job).await()
    }

    fun validateInput(field: TextInputLayout, fieldValue: String): Boolean {
        val isValid = !fieldValue.isNullOrEmpty()
        field.helperText = if (isValid) "" else required
        return isValid
    }

    fun validateSalaryInput(
        minInput: TextInputLayout,
        maxInput: TextInputLayout,
        min: Int?,
        max: Int?
    ): Boolean {
        val isMinValid = min != null
        val isMaxValid = max != null

        minInput.helperText = if (isMinValid) "" else required
        maxInput.helperText = if (isMaxValid) "" else required

        if (isMinValid && isMaxValid) {
            if (min!! > max!!) {
                minInput.helperText = "Minimum salary must be less than maximum salary"
                maxInput.helperText = "Maximum salary must be greater than minimum salary"
                return false
            }
        }

        return isMinValid && isMaxValid
    }

    private val resultLD = MutableLiveData<List<Job>>()
    private var name = ""
    private var position = emptyList<String>()

    fun getResultLD() = resultLD

    fun search(name: String) {
        this.name = name
        updateResult()
    }

    fun filterPosition(position: List<String>) {
        this.position = position
        updateResult()
    }

    fun updateResult() {
        var list = getAll()

        list = list.filter {
            it.jobName.contains(name, true)
        }

        if (position.isNotEmpty()) {
            list = list.filter { job ->
                position.any { it in job.position }
            }
        }

        resultLD.value = list
    }

}