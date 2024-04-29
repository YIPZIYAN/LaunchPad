package com.example.launchpad.job.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.Company
import com.example.launchpad.data.Job
import com.example.launchpad.data.SaveJob
import com.example.launchpad.databinding.FragmentArchivedJobBinding
import com.example.launchpad.databinding.FragmentSavedJobBinding
import com.example.launchpad.job.adapter.ArchivedJobAdapter
import com.example.launchpad.job.adapter.JobAdapter
import com.example.launchpad.job.adapter.SavedJobAdapter
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.profile.viewmodel.CompanyViewModel

class SavedJobFragment : Fragment() {

    companion object {
        fun newInstance() = SavedJobFragment()
    }

    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentSavedJobBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedJobBinding.inflate(inflater, container, false)

        val adapter = SavedJobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
            holder.binding.bookmark.visibility = View.VISIBLE
            val saveJob = jobVM.getSaveJobByUser("userID")

            saveJob.forEach {
                if (it.jobID == job.jobID) {
                    holder.binding.bookmark.isChecked = true
                }
            }
            holder.binding.bookmark.setOnCheckedChangeListener { _, _ ->
                val saveJob = SaveJob(
                    id = "userID" + "_" + job.jobID,
                    userID = "userID",
                    jobID = job.jobID,
                )
                if (holder.binding.bookmark.isChecked) {
                    jobVM.saveJob(saveJob)
                } else {
                    jobVM.unsaveJob(saveJob.id)
                }
            }

        }
        binding.rvSavedJob.adapter = adapter

        jobVM.getSaveJobLD().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noSaveJob.visibility = View.VISIBLE
            }
            it.forEach {
                it.job = jobVM.get(it.jobID) ?: Job()
                it.job.company = companyVM.get(it.job.companyID) ?: Company()
            }
            adapter.submitList(it.sortedByDescending { it.job.createdAt })
        }

        binding.topAppBar.setNavigationOnClickListener { nav.navigateUp() }

        return binding.root
    }

    private fun detail(jobID: String) {
        nav.navigate(
            R.id.jobDetailsFragment, bundleOf(
                "jobID" to jobID
            )
        )
    }


}