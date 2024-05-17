package com.example.launchpad.job_application.tab

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentTabApplicantBinding
import com.example.launchpad.viewmodel.TabApplicantViewModel

class TabApplicantFragment : Fragment() {

    companion object {
        fun newInstance() = TabApplicantFragment()
    }

    private val viewModel: TabApplicantViewModel by viewModels()
    private lateinit var binding: FragmentTabApplicantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tab_applicant, container, false)
        binding.applicant.setOnClickListener {
            findNavController().navigate(R.id.action_viewApplicantFragment_to_applicantDetailsFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabApplicant.visibility = View.VISIBLE
        binding.tabNoApplicant.visibility = View.GONE


    }
}