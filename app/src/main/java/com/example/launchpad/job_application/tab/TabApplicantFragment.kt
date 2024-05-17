package com.example.launchpad.job_application.tab

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentTabApplicantBinding
import com.example.launchpad.job_application.adapter.ApplicantAdapter
import com.example.launchpad.util.JobApplicationState
import com.example.launchpad.viewmodel.TabApplicantViewModel

class TabApplicantFragment(val jobID: String) : Fragment() {


    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentTabApplicantBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabApplicantBinding.inflate(inflater, container, false)


        val adapter = ApplicantAdapter { h, f ->
            h.binding.root.setOnClickListener {
                findNavController().navigate(
                    R.id.applicantDetailsFragment,
                    bundleOf("jobID" to jobID)
                )
            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        jobAppVM.getJobAppLD().observe(viewLifecycleOwner) { list ->
            val applicantList =
                list.filter { it.jobId == jobID && it.status == JobApplicationState.NEW.toString() }

            if (applicantList.isEmpty()) {
                binding.tabApplicant.visibility = View.INVISIBLE
                binding.tabNoApplicant.visibility = View.VISIBLE
                return@observe
            }

            applicantList.forEach { it.user = userVM.get(it.userId)!! }

            binding.numApplicant.text = applicantList.size.toString() + " applicant(s)"
            binding.tabApplicant.visibility = View.VISIBLE
            binding.tabNoApplicant.visibility = View.GONE


            adapter.submitList(applicantList)

        }

        return binding.root
    }


}