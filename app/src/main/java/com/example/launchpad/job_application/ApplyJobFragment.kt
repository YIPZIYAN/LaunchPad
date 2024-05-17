package com.example.launchpad.job_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.launchpad.data.Company
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentApplyJobBinding
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.viewmodel.ApplyJobViewModel

class ApplyJobFragment : Fragment() {

    companion object {
        fun newInstance() = ApplyJobFragment()
    }

    private lateinit var binding: FragmentApplyJobBinding
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private val jobID by lazy { arguments?.getString("jobID") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobVM.getJobsLD().observe(this) {
            var job = jobVM.get(jobID) ?: return@observe
            job.company = companyVM.get(job.companyID) ?: Company()
            binding.topAppBar.title = "Apply To ${job.company.name}"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyJobBinding.inflate(inflater, container, false)



        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }


}