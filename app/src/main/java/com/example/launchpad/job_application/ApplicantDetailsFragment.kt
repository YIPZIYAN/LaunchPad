package com.example.launchpad.job_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentApplicantDetailsBinding
import com.example.launchpad.viewmodel.ApplicantDetailsViewModel

class ApplicantDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ApplicantDetailsFragment()
    }

    private lateinit var viewModel: ApplicantDetailsViewModel
    private lateinit var binding: FragmentApplicantDetailsBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplicantDetailsBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
           nav.navigateUp()
        }

        binding.file.setOnClickListener { nav.navigate(R.id.pdfViewerFragment) }

        return binding.root
    }
}