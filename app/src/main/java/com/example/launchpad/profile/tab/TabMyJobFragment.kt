package com.example.launchpad.profile.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.JobViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentTabMyJobBinding
import com.example.launchpad.profile.adapter.MyJobAdapter

class TabMyJobFragment : Fragment() {

    private val companyVM: CompanyViewModel by activityViewModels()
    private val jobVM: JobViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    lateinit var binding: FragmentTabMyJobBinding
    lateinit var adapter: MyJobAdapter
    private val nav by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabMyJobBinding.inflate(inflater, container, false)

        initAdapter()

        jobAppVM.getJobAppLD().observe(viewLifecycleOwner) { jobAppList ->
            if (jobAppList.isEmpty()) {
                binding.tabNoApplicant.visibility = View.VISIBLE
                binding.rv.visibility = View.GONE
                return@observe
            }
            jobAppList.forEach { it.job = jobVM.get(it.jobId)!! }
            jobAppList.forEach { it.job.company = companyVM.get(it.job.companyID)!! }

            val myJobAppList = jobAppList.filter { it.userId == userVM.getAuth().uid }
                .sortedByDescending { it.createdAt }

            binding.tabNoApplicant.visibility = View.GONE
            binding.rv.visibility = View.VISIBLE

            adapter.submitList(myJobAppList)
        }


        return binding.root
    }

    private fun initAdapter() {
        adapter = MyJobAdapter { h, f ->
            h.binding.root.setOnClickListener {
                findNavController().navigate(
                    R.id.jobDetailsFragment,
                    bundleOf("jobID" to f.job.jobID)
                )
            }
        }

        binding.rv.adapter = adapter
        binding.rv.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}