package com.example.launchpad.job_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.databinding.FragmentApplicantDetailsBinding
import com.example.launchpad.viewmodel.ApplicantDetailsViewModel

class ApplicantDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ApplicantDetailsFragment()
    }

    private lateinit var viewModel: ApplicantDetailsViewModel
    private lateinit var binding: FragmentApplicantDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplicantDetailsBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}