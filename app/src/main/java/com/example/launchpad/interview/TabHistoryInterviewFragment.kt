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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentTabHistoryInterviewBinding.inflate(inflater, container, false)

        val adapter = InterviewHistoryAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        interviewVM.getInterviewLD().observe(viewLifecycleOwner) { list ->
            val interviewHistoryList =
                list.filter { it.date < DateTime.now().millis}
            Log.d("TAG", "onCreateView: $interviewHistoryList")
            if (interviewHistoryList.isEmpty()) {
//                binding.tabApplicant.visibility = View.INVISIBLE
//                binding.tabNoApplicant.visibility = View.VISIBLE
                return@observe
            }

            interviewHistoryList.forEach { it.jobApp = jobAppVM.get(it.jobAppID)!! }
            interviewHistoryList.forEach { it.jobApp.user = userVM.get(it.jobApp.userId)!! }
            interviewHistoryList.forEach { it.jobApp.job = jobVM.get(it.jobApp.jobId)!! }
            interviewHistoryList.sortedByDescending { it.date }

            binding.numApplicant.text = interviewHistoryList.size.toString() + " applicant(s)"
            binding.tabApplicant.visibility = View.VISIBLE
//            binding.tabNoApplicant.visibility = View.GONE


            adapter.submitList(interviewHistoryList)

        }


        return binding.root
    }
}