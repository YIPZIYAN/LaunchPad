package com.example.launchpad.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.firestore

data class Company(
    @DocumentId
    val id: String = "",
    val name: String = "",
//    val avatar: Blob? = Blob.fromBytes(ByteArray(0)),
    val description: String = "",
    val location: String = "",
    val year: Int = -1,
)

private val COMPANIES = Firebase.firestore.collection("company")

/*
fun COMPANY_SEEDER(ctx: Context) {
    fun getBlob(res: Int) = BitmapFactory.decodeResource(ctx.resources, res).toBlob()
    val companies = listOf(
        Company("COM1", "Msoft Marketing", "msoftmarketing@gmail.com", getBlob(R.raw.banana), "We are good company (definitely no OT)", "Bangsar South", 2016)
    )

    companies.forEach { COMPANIES.document(it.id).set(it) }
}


private val USERS = Firebase.firestore.collection("user")


fun USER_SEEDER(ctx: Context) {
    fun getBlob(res: Int) = BitmapFactory.decodeResource(ctx.resources, res).toBlob()
    val users = listOf(
        User("USER1", "Msoft Marketing", "msoftmarketing@gmail.com")
    )

    users.forEach { USERS.document(it.id!!).set(it) }
}


 */