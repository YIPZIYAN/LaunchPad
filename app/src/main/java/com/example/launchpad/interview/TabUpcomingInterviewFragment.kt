package com.example.launchpad.interview

import android.content.Intent
import android.net.Uri
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
import com.example.launchpad.data.viewmodel.InterviewViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentTabUpcomingInterviewBinding
import com.example.launchpad.interview.adapter.InterviewAdapter
import com.example.launchpad.data.viewmodel.JobViewModel
import com.example.launchpad.util.combineDateTime
import com.example.launchpad.util.toast
import org.joda.time.DateTime
import java.lang.Exception

class TabUpcomingInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = TabUpcomingInterviewFragment()
    }

    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val interviewVM: InterviewViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val jobVM: JobViewModel by activityViewModels()
    private lateinit var binding: FragmentTabUpcomingInterviewBinding
    private val nav by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabUpcomingInterviewBinding.inflate(inflater, container, false)

        val adapter = InterviewAdapter { h, f ->
            h.binding.appliedJob.setOnClickListener {
                nav.navigate(
                    R.id.jobDetailsFragment, bundleOf(
                        "jobID" to f.jobApp.job.jobID
                    )
                )
            }
            h.binding.video.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(f.video))
                try {
                    startActivity(i)
                } catch (e: Exception) {
                    toast("Invalid link.")
                }
            }
            h.binding.location.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${f.location}"))
                try {
                    startActivity(i)
                } catch (e: Exception) {
                    toast(e.message.toString())
                }
            }
            h.binding.root.setOnClickListener {
                if (userVM.isEnterprise())
                    nav.navigate(
                        R.id.scheduleInterviewFragment, bundleOf(
                            "jobAppID" to f.jobApp.id,
                            "interviewID" to f.id,
                            "action" to "EDIT"
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
            if (list.isEmpty()){
                binding.tabApplicant.visibility = View.INVISIBLE
                binding.tabNoApplicant.visibility = View.VISIBLE
                return@observe
            }

            val interviewList =
                list.filter {
                    it.date >= DateTime.now().withTime(0, 0, 0, 0).millis
                }

            interviewList.forEach { it.jobApp = jobAppVM.get(it.jobAppID)!! }
            interviewList.forEach { it.jobApp.user = userVM.get(it.jobApp.userId)!! }
            interviewList.forEach { it.jobApp.job = jobVM.get(it.jobApp.jobId)!! }

            //filter for the particular user only
            val personalInterviewList = interviewList.filter {
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

            val sortedList = personalInterviewList.sortedWith(compareBy {
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