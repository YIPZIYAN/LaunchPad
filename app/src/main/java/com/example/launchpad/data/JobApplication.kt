package com.example.launchpad.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class JobApplication(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val jobId: String = "",
    val file: Pdf = Pdf(),
    val info: String = "",
    val status: String = ""
) {
    @get:Exclude
    var user: User = User()
    @get:Exclude
    val job: Job = Job()
    @get:Exclude
    val count = 0
}

data class Pdf(
    val name: String? = "",
    val path: String? = "",
)