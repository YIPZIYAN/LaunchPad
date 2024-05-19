package com.example.launchpad.interview

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
import com.example.launchpad.data.viewmodel.InterviewViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentTabHistoryInterviewBinding
import com.example.launchpad.interview.adapter.InterviewHistoryAdapter
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.job_application.adapter.ApplicantAdapter

import com.example.launchpad.viewmodel.TabPendingInterviewViewModel
import org.joda.time.DateTime

class TabHistoryInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = TabHistoryInterviewFragment()
    }

    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val interviewVM: InterviewViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val jobVM: JobViewModel by activityViewModels()
    private lateinit var binding: FragmentTabHistoryInterviewBinding
    private val nav by lazy { findNavController() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentTabHistoryInterviewBinding.inflate(inflater, container, false)

        val adapter = InterviewHistoryAdapter { h, f ->
            h.binding.appliedJob.setOnClickListener {
                nav.navigate(
                    R.id.jobDetailsFragment, bundleOf(
                        "jobID" to f.jobApp.job.jobID
                    )
                )
            }
            h.binding.root.setOnClickListener {
                if (userVM.isEnterprise())
                    nav.navigate(
                        R.id.scheduleInterviewFragment, bundleOf(
                            "jobAppID" to f.jobApp.id,
                            "interviewID" to f.id,
                            "action" to "VIEW"
                        )
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

        interviewVM.getInterviewLD().observe(viewLifecycleOwner) { list ->
            val interviewHistoryList =
                list.filter {
                    it.date < DateTime.now().minusDays(1)
                        .withTime(23, 59, 59, 999).millis
                }


            interviewHistoryList.forEach { it.jobApp = jobAppVM.get(it.jobAppID)!! }
            interviewHistoryList.forEach { it.jobApp.user = userVM.get(it.jobApp.userId)!! }
            interviewHistoryList.forEach { it.jobApp.job = jobVM.get(it.jobApp.jobId)!! }

            //filter for the particular user only
            val personalInterviewList = interviewHistoryList.filter {
                if (userVM.isEnterprise())
                    it.jobApp.job.companyID == userVM.getUserLD().value!!.company_id
                else
                    it.jobApp.userId == userVM.getAuth().uid
            }.filter {
                it.jobApp.job.deletedAt == 0L
            }

            if (personalInterviewList.isEmpty()) {
                binding.tabApplicant.visibility = View.INVISIBLE
                binding.tabNoApplicant.visibility = View.VISIBLE
                return@observe
            }


            binding.numApplicant.text = personalInterviewList.size.toString() + " interview(s)"
            binding.tabApplicant.visibility = View.VISIBLE
            binding.tabNoApplicant.visibility = View.GONE


            adapter.submitList(personalInterviewList.sortedByDescending { it.date })

        }


        return binding.root
    }
}