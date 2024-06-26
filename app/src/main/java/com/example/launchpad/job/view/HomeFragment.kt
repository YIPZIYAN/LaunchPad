package com.example.launchpad.job.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.Company
import com.example.launchpad.data.SaveJob
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentHomeBinding
import com.example.launchpad.job.adapter.JobAdapter
import com.example.launchpad.data.viewmodel.JobViewModel
import com.example.launchpad.util.dialogCompanyNotRegister
import com.example.launchpad.util.disable
import com.example.launchpad.util.getToken
import com.example.launchpad.util.loadingDialog
import com.google.android.material.search.SearchView
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class HomeFragment : Fragment(), BottomSheetListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: JobAdapter
    private lateinit var svAdapter: JobAdapter
    private val nav by lazy { findNavController() }
    private val jobVM: JobViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()
    private var chipPositionState = mutableListOf<String>()
    private var chipJobTypeState = mutableListOf<String>()
    private var chipWorkplaceState = mutableListOf<String>()
    private var chipSalaryState = mutableListOf("0", "999999")
    private var isSearching = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.loadingLayout.visibility = View.VISIBLE
        binding.btnSavedJob.visibility = View.VISIBLE
        getGreeting()

        userVM.getUserLD().observe(viewLifecycleOwner) {
            if (it == null) {
                lifecycleScope.launch {
                    userVM.set(userVM.getAuth())
                }
                return@observe
            }

            getToken().observe(viewLifecycleOwner) { token ->
                lifecycleScope.launch {
                    if (userVM.getAuth().token != token) {
                        userVM.setToken(token)
                    }
                }
            }

            binding.username.text = it.name
            //-----------------------------------------------------------
            // Company
            updateUI()
            binding.btnSavedJob.visibility = View.VISIBLE

            //-----------------------------------------------------------
            // Show Job List & Save Job
            adapter = setAdapter(it)
            svAdapter = setAdapter(it)

            binding.rvJobCard.adapter = adapter
            binding.rvSearchResult.adapter = svAdapter

            jobVM.updateResult()
            jobVM.reloadJob()
        }

        jobVM.getJobsLD().observe(viewLifecycleOwner) { jobList ->
            if (userVM.getUserLD().value == null) return@observe
            binding.loadingLayout.visibility = View.GONE
            if (jobList.isEmpty()) return@observe

            companyVM.getCompaniesLD().observe(viewLifecycleOwner) { company ->
                if (company != null)
                    jobList.forEach { job ->
                        job.company = companyVM.get(job.companyID) ?: Company()
                    }
            }


            var sortedJobList = jobList.sortedByDescending { job ->
                job.createdAt
            }.filter { it.deletedAt == 0L }
            if (userVM.isEnterprise()) {
                sortedJobList =
                    sortedJobList.filter { it.companyID == userVM.getUserLD().value!!.company_id }

            }

            adapter.submitList(sortedJobList)
        }

        jobVM.getResultLD().observe(viewLifecycleOwner) { jobList ->
            if (userVM.getUserLD().value == null) return@observe
            if (jobList.isEmpty() && !isSearching) return@observe

            companyVM.getCompaniesLD().observe(viewLifecycleOwner) { company ->
                if (company != null)
                    jobList.forEach { job ->
                        job.company = companyVM.get(job.companyID) ?: Company()
                    }
            }


            var sortedJobList = jobList.sortedByDescending { job ->
                job.createdAt
            }
            if (userVM.isEnterprise()) {
                sortedJobList =
                    sortedJobList.filter { it.companyID == userVM.getUserLD().value!!.company_id }
            }

            svAdapter.submitList(sortedJobList)
        }


        //-----------------------------------------------------------
        // Refresh
        binding.refresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            svAdapter.notifyDataSetChanged()
            binding.refresh.isRefreshing = false
        }
        //-----------------------------------------------------------
        // Search And Filter


        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                clearFilter()
                jobVM.clearSearch()
            }
        }

        binding.searchView.editText.doOnTextChanged { text, _, _, _ ->
            isSearching = true
            jobVM.search(text.toString())
        }

        binding.chipPosition.setOnClickListener { chipPosition() }

        binding.chipJobType.setOnClickListener { chipJobType() }

        binding.chipWorkplace.setOnClickListener { chipWorkplace() }

        binding.chipSalary.setOnClickListener { chipSalary() }

        return binding.root
    }

    private fun setAdapter(it: User): JobAdapter {
        return JobAdapter { holder, job ->
            holder.binding.root.setOnClickListener { detail(job.jobID) }
            if (!userVM.isEnterprise()) {
                holder.binding.bookmark.visibility = View.VISIBLE
                val saveJob = jobVM.getSaveJobByUser(it.uid)
                saveJob.forEach { jobs ->
                    if (jobs.jobID == job.jobID) {
                        holder.binding.bookmark.isChecked = true
                    }
                }
                holder.binding.bookmark.setOnCheckedChangeListener { _, _ ->
                    val saveJob = SaveJob(
                        id = it.uid + "_" + job.jobID,
                        userID = it.uid,
                        jobID = job.jobID,
                    )
                    if (holder.binding.bookmark.isChecked) {
                        jobVM.saveJob(saveJob)
                    } else {
                        jobVM.unsaveJob(saveJob.id)
                    }
                }
            }
        }
    }

    private fun updateUI() {
        if (userVM.isEnterprise()) {
            binding.homeTitle.text = resources.getString(R.string.your_posted_job)
            binding.btnSavedJob.text = resources.getString(R.string.Archived)
            binding.btnSavedJob.setOnClickListener {
                nav.navigate(R.id.action_homeFragment_to_archivedJobFragment)
            }
            binding.btnPostJob.visibility = View.VISIBLE
            binding.btnPostJob.setOnClickListener {
                if (!userVM.isCompanyRegistered()) {
                    dialogCompanyNotRegister(
                        userVM.isEnterprise() && !userVM.isCompanyRegistered(),
                        nav
                    )
                    return@setOnClickListener
                }
                nav.navigate(R.id.action_homeFragment_to_postJobFragment)
            }
        } else {
            binding.homeTitle.text = resources.getString(R.string.recent_job_list)
            binding.btnSavedJob.text = resources.getString(R.string.saved_job)
            binding.btnSavedJob.setOnClickListener {
                nav.navigate(R.id.action_homeFragment_to_savedJobFragment)
            }
        }
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
        isSearching = false
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

    private fun getGreeting() {
        val now = DateTime.now().hourOfDay
        when (now) {
            in 0..4 -> binding.lblGreeting.text = resources.getString(R.string.TimeToSleep)
            in 5..11 -> binding.lblGreeting.text = resources.getString(R.string.GoodMorning)
            in 12..17 -> binding.lblGreeting.text = resources.getString(R.string.GoodAfternoon)
            else -> binding.lblGreeting.text = resources.getString(R.string.GoodEvening)
        }
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
        isSearching = true

    }
}


