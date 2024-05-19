package com.example.launchpad.interview

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.InterviewViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentTabHistoryInterviewBinding
import com.example.launchpad.databinding.FragmentTabUpcomingInterviewBinding
import com.example.launchpad.interview.adapter.InterviewAdapter
import com.example.launchpad.interview.adapter.InterviewHistoryAdapter
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.util.combineDateTime
import com.example.launchpad.viewmodel.TabUpcomingInterviewViewModel
import org.joda.time.DateTime

class TabUpcomingInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = TabUpcomingInterviewFragment()
    }

    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val interviewVM: InterviewViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val jobVM: JobViewModel by activityViewModels()
    private lateinit var binding: FragmentTabUpcomingInterviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabUpcomingInterviewBinding.inflate(inflater, container, false)

        val adapter = InterviewAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        interviewVM.getInterviewLD().observe(viewLifecycleOwner) { list ->
            val interviewList =
                list.filter {
                    it.date >= DateTime.now().withTime(0, 0, 0, 0).millis
                }

            if (interviewList.isEmpty()) {
//                binding.tabApplicant.visibility = View.INVISIBLE
//                binding.tabNoApplicant.visibility = View.VISIBLE
                return@observe
            }


            interviewList.forEach { it.jobApp = jobAppVM.get(it.jobAppID)!! }
            interviewList.forEach { it.jobApp.user = userVM.get(it.jobApp.userId)!! }
            interviewList.forEach { it.jobApp.job = jobVM.get(it.jobApp.jobId)!! }


            binding.numApplicant.text = interviewList.size.toString() + " applicant(s)"
            binding.tabApplicant.visibility = View.VISIBLE
//            binding.tabNoApplicant.visibility = View.GONE

            val sortedList = interviewList.sortedWith(compareBy {
                combineDateTime(
                    it.date,
                    it.startTime.hour,
                    it.startTime.minutes
                )
            })

            adapter.submitList(sortedList)


        }

        return binding.root
    }
}