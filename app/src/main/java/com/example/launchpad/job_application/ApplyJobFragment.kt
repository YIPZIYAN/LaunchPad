package com.example.launchpad.job_application

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.data.Company
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.databinding.FragmentApplyJobBinding
import com.example.launchpad.job.viewmodel.JobViewModel

class ApplyJobFragment : Fragment() {

    companion object {
        fun newInstance() = ApplyJobFragment()
    }

    private lateinit var binding: FragmentApplyJobBinding
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val jobID by lazy { arguments?.getString("jobID") ?: "" }
    private lateinit var filePath: Uri


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

        binding.btnUploadResume.setOnClickListener { getContent.launch("application/pdf") }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            filePath = uri
            binding.fileName.text =
                uri?.let { DocumentFile.fromSingleUri(requireActivity(), it) }.toString()
        }

}