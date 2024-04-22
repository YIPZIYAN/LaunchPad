package com.example.launchpad.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.launchpad.data.Job
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

class JobViewModel(val app: Application) : AndroidViewModel(app) {
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

    fun get(jobID: String) = jobsLD.value?.find { it.jobID == jobID }

    fun set(job: Job) {
        JOBS.document().set(job);
    }

    fun validate(job: Job): Map<String, String> {
        val errors = mutableMapOf<String, String>()
        val required = "* Required"
        if (job.jobName?.isEmpty() == true) {
            errors["jobName"] = required
        }

        if (job.position?.isEmpty() == true) {
            errors["position"] = required
        }

        if (job.jobType?.isEmpty() == true) {
            errors["jobType"] = required
        }

        if (job.workplace?.isEmpty() == true) {
            errors["workplace"] = required
        }

        if (job.minSalary?.isNaN() == true) {
            errors["minSalary"] = required
        }

        if (job.maxSalary?.isNaN() == true) {
            errors["maxSalary"] = required
        }

        if (job.qualification?.isEmpty() == true) {
            errors["qualification"] = required
        }

        if (job.experience?.isEmpty() == true) {
            errors["experience"] = required
        }

        if (job.description?.isEmpty() == true) {
            errors["description"] = required
        }

        return errors
    }

}