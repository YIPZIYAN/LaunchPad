package com.example.launchpad.job.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.example.launchpad.util.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.data.Job
import com.example.launchpad.databinding.FragmentPostJobBinding
import com.example.launchpad.job.viewmodel.JobViewModel
import org.joda.time.DateTime

class PostJobFragment : Fragment() {

    companion object {
        fun newInstance() = PostJobFragment()
    }

    private val viewModel: JobViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentPostJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostJobBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            nav.navigateUp()
        }
        binding.btnPost.setOnClickListener { post() }
        validateOnTextChanged()
        return binding.root
    }

    private fun validateOnTextChanged() {
        binding.edtJobName.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblJobName,
                text.toString().trim()
            )
        }
        binding.edtPosition.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblPosition,
                text.toString().trim()
            )
        }
        binding.edtJobType.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblJobType,
                text.toString().trim()
            )
        }
        binding.edtWorkplace.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblWorkplace,
                text.toString().trim()
            )
        }
        binding.edtMinSalary.doOnTextChanged { text, _, _, _ ->

            val min = text.toString().toIntOrNull()
            val max = binding.edtMaxSalary.text.toString().toIntOrNull()

            viewModel.validateSalaryInput(binding.lblMinSalary, binding.lblMaxSalary, min, max)

        }
        binding.edtMaxSalary.doOnTextChanged { text, _, _, _ ->

            val min = binding.edtMinSalary.text.toString().toIntOrNull()
            val max = text.toString().toIntOrNull()

            viewModel.validateSalaryInput(binding.lblMinSalary, binding.lblMaxSalary, min, max)

        }
        binding.edtQualification.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblQualification,
                text.toString().trim()
            )
        }
        binding.edtExperience.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblExperience,
                text.toString().trim()
            )
        }
        binding.edtJobDescription.doOnTextChanged { text, _, _, _ ->
            viewModel.validateInput(
                binding.lblJobDesc,
                text.toString().trim()
            )
        }
    }


    private fun post() {

        val job = Job(
            jobName = binding.edtJobName.text.toString().trim(),
            position = binding.edtPosition.text.toString(),
            jobType = binding.edtJobType.text.toString(),
            workplace = binding.edtWorkplace.text.toString(),
            minSalary = binding.edtMinSalary.text.toString().toIntOrNull(),
            maxSalary = binding.edtMaxSalary.text.toString().toIntOrNull(),
            qualification = binding.edtQualification.text.toString(),
            experience = binding.edtExperience.text.toString().toIntOrNull(),
            description = binding.edtJobDescription.text.toString().trim(),
            requirement = binding.edtJobRequirement.text.toString().trim(),
            postTime = DateTime.now().millis
        )

        val validations = listOf(
            { viewModel.validateInput(binding.lblJobName, job.jobName) },
            { viewModel.validateInput(binding.lblPosition, job.position) },
            { viewModel.validateInput(binding.lblJobType, job.jobType) },
            { viewModel.validateInput(binding.lblWorkplace, job.workplace) },
            {
                viewModel.validateSalaryInput(
                    binding.lblMinSalary,
                    binding.lblMaxSalary,
                    job.minSalary,
                    job.maxSalary
                )
            },
            { viewModel.validateInput(binding.lblQualification, job.qualification) },
            { viewModel.validateInput(binding.lblExperience, job.experience.toString()) },
            { viewModel.validateInput(binding.lblJobDesc, job.description) },
        )

        val isValid = validations.all { it() }

        if (!isValid)
            return

        dialog("Post Job", "Are you sure want to post the job ?",
            onPositiveClick = { _, _ ->
                viewModel.set(job)
                snackbar("Job Posted Successfully")
                nav.navigateUp()
            })
    }

}