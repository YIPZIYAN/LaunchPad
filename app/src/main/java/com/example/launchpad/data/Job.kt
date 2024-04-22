package com.example.launchpad.data

import com.google.firebase.firestore.DocumentId

data class Job(
    @DocumentId
    val jobID: String = "",
    val jobName: String = "",
    val position: String = "",
    val jobType: String = "",
    val workplace: String = "",
    val minSalary: Int? = null,
    val maxSalary: Int? = null,
    val qualification: String = "",
    val experience: Int? = null,
    val description: String = "",
    val requirement: String = "",
    val postTime: Long? = null,
)
