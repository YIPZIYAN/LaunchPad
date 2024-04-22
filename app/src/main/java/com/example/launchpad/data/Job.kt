package com.example.launchpad.data

import com.google.firebase.firestore.DocumentId

data class Job(
    @DocumentId
    val jobID: String = "",
    val jobName: String? = "",
    val position: String? = "",
    val jobType: String? = "",
    val workplace: String? = "",
    val minSalary: Double? = 0.0,
    val maxSalary: Double? = 0.0,
    val qualification: String? = "",
    val experience: String? = "",
    val description: String? = "",
    val requirement: String? = "",
    val postTime: Long? = null,
)
