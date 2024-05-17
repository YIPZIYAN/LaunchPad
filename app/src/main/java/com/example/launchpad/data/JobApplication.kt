package com.example.launchpad.data

import com.google.firebase.firestore.DocumentId

data class JobApplication(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val jobId: String = "",
    val file: Pdf = Pdf(),
    val info: String = "",
)

data class Pdf(
    val name: String? = "",
    val path: String? = "",
)