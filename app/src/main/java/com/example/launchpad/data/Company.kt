package com.example.launchpad.data

import android.content.Context
import android.graphics.BitmapFactory
import com.example.launchpad.R
import com.example.launchpad.util.toBlob
import com.google.firebase.Firebase
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore

data class Company(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatar: Blob = Blob.fromBytes(ByteArray(0)),
    val description: String = "",
    val location: String = "",
    val year: Int = -1,
)
/*
private val COMPANIES = Firebase.firestore.collection("company")


fun COMPANY_SEEDER(ctx: Context) {
    fun getBlob(res: Int) = BitmapFactory.decodeResource(ctx.resources, res).toBlob()
    val companies = listOf(
        Company("COM1", "Msoft Marketing", "msoftmarketing@gmail.com", getBlob(R.raw.banana), "We are good company (definitely no OT)", "Bangsar South", 2016)
    )

    companies.forEach { COMPANIES.document(it.id).set(it) }
}
*/