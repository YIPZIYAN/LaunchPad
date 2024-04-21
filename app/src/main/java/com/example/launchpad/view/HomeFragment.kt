package com.example.launchpad.view

import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.viewmodel.HomeViewModel
import com.example.launchpad.R
import com.example.launchpad.adapter.JobAdapter
import com.example.launchpad.databinding.FragmentHomeBinding
import com.example.launchpad.databinding.FragmentMyProfileBinding
import com.example.launchpad.view.LoginFragment.Companion.userType

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = JobAdapter { h, job ->
            h.binding.jobCard.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_jobDetailsFragment)
            }
        }
        binding.rvJobCard.adapter = adapter

        viewModel.getJobsLD().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.btnPostJob.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_postJobFragment)
        }

        // company
        if (userType == 0) {
            binding.homeTitle.text = resources.getString(R.string.your_posted_job)
            binding.btnPostJob.visibility = View.VISIBLE
            binding.btnSavedJob.visibility = View.GONE
        }

        return binding.root
    }


}