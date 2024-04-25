package com.example.launchpad.profile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.Company
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase

class CompanyViewModel : ViewModel() {
    private val COMPANIES = Firebase.firestore.collection("company")
    private val companiesLD = MutableLiveData<List<Company>>()
    private var listener : ListenerRegistration? = null

    init {
        listener = COMPANIES.addSnapshotListener { snap, _ ->
            companiesLD.value = snap?.toObjects()
        }
    }

    override fun onCleared() {
        listener?.remove()
    }

    fun init() = Unit

    fun getCompaniesLD() = companiesLD

    fun getAll() = companiesLD.value ?: emptyList()

    fun get(id: String) = getAll().find { it.id == id }

    fun set(company: Company) {
        COMPANIES.document().set(company);
    }


}