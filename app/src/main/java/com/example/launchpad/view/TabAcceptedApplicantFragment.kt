package com.example.launchpad.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.launchpad.R
import com.example.launchpad.viewmodel.TabAcceptedApplicantViewModel

class TabAcceptedApplicantFragment : Fragment() {

    companion object {
        fun newInstance() = TabAcceptedApplicantFragment()
    }

    private val viewModel: TabAcceptedApplicantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tab_accepted_applicant, container, false)
    }
}