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
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.databinding.FragmentArchivedJobBinding
import com.example.launchpad.job.adapter.ArchivedJobAdapter
import com.example.launchpad.job.viewmodel.JobViewModel

class ArchivedJobFragment : Fragment() {

    companion object {
        fun newInstance() = ArchivedJobFragment()
    }

    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentArchivedJobBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArchivedJobBinding.inflate(inflater, container, false)

        val adapter = ArchivedJobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
        }
        binding.rvArchivedJob.adapter = adapter

        jobVM.updateArchived()

        jobVM.getArchivedLD().observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noArchivedJob.visibility = View.VISIBLE
            }
            it.forEach { it.company = companyVM.get(it.companyID) ?: Company() }
            adapter.submitList(it.sortedByDescending { it.deletedAt })
        }

        binding.topAppBar.setNavigationOnClickListener { nav.navigateUp() }

        return binding.root
    }

    private fun detail(jobID: String) {
        nav.navigate(
            R.id.jobDetailsFragment, bundleOf(
                "jobID" to jobID,
                "isArchived" to true
            )
        )
    }

}