package com.example.launchpad.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.launchpad.util.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.data.Job
import com.example.launchpad.databinding.FragmentPostJobBinding
import com.example.launchpad.viewmodel.JobViewModel
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
        return binding.root
    }

    private fun post() {
        val job = Job(
            jobName = binding.edtJobName.text.toString().trim(),
            position = binding.edtPosition.text.toString(),
            jobType = binding.edtJobType.text.toString(),
            workplace = binding.edtWorkplace.text.toString(),
            minSalary = binding.edtMinSalary.text.toString().toDoubleOrNull() ?: -1.0,
            maxSalary = binding.edtMaxSalary.text.toString().toDoubleOrNull() ?: -1.0,
            qualification = binding.edtQualification.text.toString(),
            experience = binding.edtExperience.text.toString().trim(),
            description = binding.edtJobDescription.text.toString().trim(),
            requirement = binding.edtJobRequirement.text.toString().trim(),
            postTime = DateTime.now().millis
        )

        val errors = viewModel.validate(job)
        if (errors.isNotEmpty()) {
            binding.lblJobName.helperText = errors["jobName"]
            return
        }


        viewModel.set(job)
        toast("Job Posted")
        nav.navigateUp()
    }

}