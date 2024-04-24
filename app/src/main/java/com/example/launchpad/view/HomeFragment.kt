package com.example.launchpad.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.viewmodel.JobViewModel
import com.example.launchpad.R
import com.example.launchpad.adapter.JobAdapter
import com.example.launchpad.databinding.FragmentHomeBinding
import com.example.launchpad.auth.view.LoginFragment.Companion.userType
import com.example.launchpad.data.Company
import com.example.launchpad.profile.viewmodel.CompanyViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val nav by lazy { findNavController() }
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //COMPANY_SEEDER(requireContext())

        val adapter = JobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
        }

        binding.rvJobCard.adapter = adapter

        jobVM.getResultLD().observe(viewLifecycleOwner) {
            it.forEach { it.company = companyVM.get(it.companyID) ?: Company() }
            adapter.submitList(it.sortedByDescending { it.postTime })
        }

        binding.btnPostJob.setOnClickListener {
            nav.navigate(R.id.action_homeFragment_to_postJobFragment)
        }

        binding.refresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            binding.refresh.isRefreshing = false
        }

        // company
        if (userType == 0) {
            binding.homeTitle.text = resources.getString(R.string.your_posted_job)
            binding.btnPostJob.visibility = View.VISIBLE
            binding.btnSavedJob.visibility = View.GONE
        }

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