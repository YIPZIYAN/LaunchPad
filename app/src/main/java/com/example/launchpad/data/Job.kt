package com.example.launchpad.data

import com.google.firebase.firestore.DocumentId

data class Job(
    @DocumentId
    val jobID: String = "",
    val jobName: String = "",
    val location: String = "",
    val minSalary: Double = 0.0,
    val maxSalary: Double = 0.0,
    val jobType: String = "",
    val workplace: String = "",
    val position: String = "",
)
