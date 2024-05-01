package com.example.launchpad.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.launchpad.data.Company
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class CompanyViewModel : ViewModel() {
    private val COMPANIES = Firebase.firestore.collection("company")
    private val _companyLD = MutableLiveData<List<Company>>()

    private var listener: ListenerRegistration? = null

    init {
        listener = COMPANIES.addSnapshotListener { snap, _ ->
            _companyLD.value = snap?.toObjects()
        }
    }

    override fun onCleared() {
        listener?.remove()
    }

    fun init() = Unit

    fun getCompaniesLD() = _companyLD

    fun getAll() = _companyLD.value ?: emptyList()

    fun get(companyID: String) = getAll().find { it.id == companyID }

    suspend fun set(company: Company) {
        COMPANIES.document().set(company).await()
    }

    suspend fun update(company: Company) {
        COMPANIES.document(company.id).set(company).await()
    }

}