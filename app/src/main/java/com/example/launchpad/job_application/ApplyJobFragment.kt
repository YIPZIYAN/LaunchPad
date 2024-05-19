package com.example.launchpad.job_application

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.Company
import com.example.launchpad.data.JobApplication
import com.example.launchpad.data.Pdf
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentApplyJobBinding
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.util.JobApplicationState
import com.example.launchpad.util.dialog
import com.example.launchpad.util.showFileSize
import com.example.launchpad.util.snackbar
import com.example.launchpad.util.toast
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

class ApplyJobFragment : Fragment() {

    companion object {
        fun newInstance() = ApplyJobFragment()
    }

    private lateinit var binding: FragmentApplyJobBinding
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by viewModels()
    private val userVM: UserViewModel by activityViewModels()

    private val jobID by lazy { arguments?.getString("jobID") ?: "" }
    private var fileUri: Uri? = null
    private val nav by lazy { findNavController() }

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
        binding.file.visibility = View.GONE

        binding.btnUploadResume.setOnClickListener { getContent.launch("application/pdf") }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSubmit.setOnClickListener { submit() }

        jobAppVM.progress.observe(viewLifecycleOwner) {
            binding.progressBar.progress = it

            binding.btnSubmit.isEnabled = false
            binding.btnSubmit.isClickable = false

            binding.btnUploadResume.isClickable = false
            binding.btnUploadResume.isClickable = false
        }

        jobAppVM.response.observe(viewLifecycleOwner) {
            if (it != null) {
                toast(it)
                binding.progressBar.progress = 0
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.isClickable = true

                binding.btnUploadResume.isClickable = true
                binding.btnUploadResume.isClickable = true
            }
        }

        jobAppVM.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                nav.popBackStack(R.id.homeFragment, false)
                snackbar("Job Applied Successfully")
            }
        }

        return binding.root
    }

    private fun submit() {
        if (fileUri == null) {
            toast("Please Upload Your Resume!")
            return
        }

        dialog("Apply Job", "Are you sure to apply this job?",
            onPositiveClick = { _, _ ->
                upload()
            })

    }

    private fun upload() {
        lifecycleScope.launch {
            jobAppVM.uploadResume(fileUri!!, binding.fileName.text.toString())
        }

        jobAppVM.resume.observe(viewLifecycleOwner) {
            Log.d("TAG", "submit: ")
            if (it.equals(Pdf())) return@observe

            val jobApp = JobApplication(
                userId = userVM.getAuth().uid,
                jobId = jobID,
                file = it,
                info = binding.edtInfo.text.toString().trim(),
                status = JobApplicationState.NEW.toString(),
            )

            lifecycleScope.launch {
                jobAppVM.set(jobApp)
            }
        }
    }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult

            fileUri = uri
            binding.file.visibility = View.VISIBLE

            val file = uri.let { DocumentFile.fromSingleUri(requireActivity(), it) }!!
            binding.fileName.text = file.name.toString()
            binding.fileSize.text = showFileSize(file.length())

        }

}