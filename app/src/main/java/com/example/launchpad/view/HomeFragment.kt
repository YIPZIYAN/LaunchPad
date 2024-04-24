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

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var binding: FragmentHomeBinding
    private val nav by lazy { findNavController() }
    private val viewModel: JobViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = JobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
        }

        binding.rvJobCard.adapter = adapter

        viewModel.getJobsLD().observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs.sortedByDescending { it.postTime })
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