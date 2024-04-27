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
import com.google.android.material.search.SearchView

class HomeFragment : Fragment(), BottomSheetListener {

    private lateinit var binding: FragmentHomeBinding
    private val nav by lazy { findNavController() }
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private var chipPositionState = mutableListOf<String>()
    private var chipJobTypeState = mutableListOf<String>()
    private var chipWorkplaceState = mutableListOf<String>()
    private var chipSalaryState = mutableListOf("0", "999999")

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
        
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                jobVM.clearSearch()
                clearFilter()
            }
        }

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            jobVM.search(text.toString())
        }

        binding.chipPosition.setOnClickListener { chipPosition() }

        binding.chipJobType.setOnClickListener { chipJobType() }

        binding.chipWorkplace.setOnClickListener { chipWorkplace() }

        binding.chipSalary.setOnClickListener { chipSalary() }

        binding.btnPostJob.setOnClickListener {
            nav.navigate(R.id.action_homeFragment_to_postJobFragment)
        }

        binding.refresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            svAdapter.notifyDataSetChanged()
            binding.refresh.isRefreshing = false
        }

        // company
        if (userType == 0) {
            binding.homeTitle.text = resources.getString(R.string.your_posted_job)
            binding.btnPostJob.visibility = View.VISIBLE
            binding.btnSavedJob.text = resources.getString(R.string.Archived)
            binding.btnSavedJob.setOnClickListener {
                nav.navigate(R.id.action_homeFragment_to_archivedJobFragment)
            }
        }

        return binding.root
    }

    private fun clearFilter() {
        chipPositionState = emptyList<String>().toMutableList()
        chipJobTypeState = emptyList<String>().toMutableList()
        chipWorkplaceState = emptyList<String>().toMutableList()
        chipSalaryState = mutableListOf("0", "999999")
        binding.chipPosition.isChecked = false
        binding.chipJobType.isChecked = false
        binding.chipWorkplace.isChecked = false
        binding.chipSalary.isChecked = false
        binding.chipPosition.text = "Position"
        binding.chipJobType.text = "Job Type"
        binding.chipWorkplace.text = "Workplace"
        binding.chipSalary.text = "Salary"
    }

    private fun chipPosition() {
        val bottomSheetFragment = PositionBottomSheetFragment(chipPositionState)
        bottomSheetFragment.setListener(this, BottomSheetListener.Type.POSITION)
        bottomSheetFragment.show(parentFragmentManager, PositionBottomSheetFragment.TAG)
    }

    private fun chipJobType() {
        val bottomSheetFragment = JobTypeBottomSheetFragment(chipJobTypeState)
        bottomSheetFragment.setListener(this, BottomSheetListener.Type.JOB_TYPE)
        bottomSheetFragment.show(parentFragmentManager, JobTypeBottomSheetFragment.TAG)
    }

    private fun chipWorkplace() {
        val bottomSheetFragment = WorkplaceBottomSheetFragment(chipWorkplaceState)
        bottomSheetFragment.setListener(this, BottomSheetListener.Type.WORKPLACE)
        bottomSheetFragment.show(parentFragmentManager, WorkplaceBottomSheetFragment.TAG)
    }

    private fun chipSalary() {
        val bottomSheetFragment = SalaryBottomSheetFragment(chipSalaryState)
        bottomSheetFragment.setListener(this, BottomSheetListener.Type.SALARY)
        bottomSheetFragment.show(parentFragmentManager, SalaryBottomSheetFragment.TAG)
    }

    private fun detail(jobID: String) {
        nav.navigate(
            R.id.jobDetailsFragment, bundleOf(
                "jobID" to jobID
            )
        )
    }

    override fun onValueSelected(value: List<String>, type: BottomSheetListener.Type) {

        when (type) {
            BottomSheetListener.Type.POSITION -> {
                jobVM.filterPosition(value)
                chipPositionState = value.toMutableList()
                binding.chipPosition.isChecked = value.isNotEmpty()
                binding.chipPosition.text = "Position"
                if (value.isNotEmpty()) {
                    binding.chipPosition.text =
                        if (value.count() > 1) "${value[0]}+${value.count() - 1}" else value[0]
                }
            }

            BottomSheetListener.Type.JOB_TYPE -> {
                jobVM.filterJobType(value)
                chipJobTypeState = value.toMutableList()
                binding.chipJobType.isChecked = value.isNotEmpty()
                binding.chipJobType.text = "Job Type"
                if (value.isNotEmpty()) {
                    binding.chipJobType.text =
                        if (value.count() > 1) "${value[0]}+${value.count() - 1}" else value[0]
                }
            }

            BottomSheetListener.Type.WORKPLACE -> {
                jobVM.filterWorkplace(value)
                chipWorkplaceState = value.toMutableList()
                binding.chipWorkplace.isChecked = value.isNotEmpty()
                binding.chipWorkplace.text = "Workplace"
                if (value.isNotEmpty()) {
                    binding.chipWorkplace.text =
                        if (value.count() > 1) "${value[0]}+${value.count() - 1}" else value[0]
                }
            }

            BottomSheetListener.Type.SALARY -> {
                val isDefaultValue = value[0].toInt() == 0 && value[1].toInt() == 999999
                jobVM.filterSalary(value)
                chipSalaryState = value.toMutableList()
                binding.chipSalary.isChecked = !isDefaultValue
                binding.chipSalary.text = "Salary"
                if (!isDefaultValue) {
                    binding.chipSalary.text =
                         "RM ${value[0]} - RM ${value[1]}"
                }
            }
        }

    }

}


