package com.example.launchpad.job.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.R
import com.example.launchpad.job.adapter.JobAdapter
import com.example.launchpad.databinding.FragmentHomeBinding
import com.example.launchpad.auth.view.LoginFragment.Companion.userType
import com.example.launchpad.data.Company
import com.example.launchpad.profile.viewmodel.CompanyViewModel


class HomeFragment : Fragment(), BottomSheetListener {

    private lateinit var binding: FragmentHomeBinding
    private val nav by lazy { findNavController() }
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private var checkedState = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val adapter = JobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
        }

        binding.rvJobCard.adapter = adapter

        val svAdapter = JobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
        }

        binding.rvSearchResult.adapter = adapter

        jobVM.getResultLD().observe(viewLifecycleOwner) {
            it.forEach { it.company = companyVM.get(it.companyID) ?: Company() }
            adapter.submitList(it.sortedByDescending { it.createdAt })
            svAdapter.submitList(it.sortedByDescending { it.createdAt })
        }

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchBar.text = binding.searchView.text
            binding.searchView.hide()
            jobVM.search(binding.searchBar.text.toString())
            false
        }

        binding.chipPosition.setOnClickListener { chipClicked() }


        binding.searchView.editText.doOnTextChanged { text, start, before, count ->
            jobVM.search(text.toString())

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
            binding.btnSavedJob.text = resources.getString(R.string.Archived)
        }

        return binding.root
    }

    private fun chipClicked() {
        val bottomSheetFragment = PositionBottomSheetFragment(checkedState)
        bottomSheetFragment.setListener(this)
        bottomSheetFragment.show(parentFragmentManager, PositionBottomSheetFragment.TAG)
    }

    private fun detail(jobID: String) {
        nav.navigate(
            R.id.jobDetailsFragment, bundleOf(
                "jobID" to jobID
            )
        )
    }

    override fun onValueSelected(value: List<String>) {
        jobVM.filterPosition(value)
        checkedState = value.toMutableList()
        binding.chipPosition.isChecked = value.isNotEmpty()
        if (value.isNotEmpty()) {
            binding.chipPosition.text = if (value.count() > 1) "${value[0]}+${value.count() - 1}" else value[0]
        }
    }

}


