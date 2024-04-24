package com.example.launchpad.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.launchpad.R
import com.example.launchpad.data.Job
import com.example.launchpad.databinding.FragmentTabJobDetailsBinding
import com.example.launchpad.viewmodel.JobViewModel
import com.example.launchpad.viewmodel.TabJobDetailsViewModel


class TabJobDetailsFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "position"
        private const val ARG_JOBID = "jobID"

        fun newInstance(position: Int, jobID: String): TabJobDetailsFragment {
            val fragment = TabJobDetailsFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            args.putString(ARG_JOBID, jobID)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: JobViewModel by activityViewModels()
    private lateinit var binding: FragmentTabJobDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_tab_job_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION, 0) ?: 0
        val jobID = arguments?.getString(ARG_JOBID) ?: ""

        val job = viewModel.get(jobID) ?: return

        binding.lblJobDesc.text = job.description
        binding.lblRequirements.text = if (job.requirement == "") "-" else job.requirement
        binding.lblPosition.text = job.position
        binding.lblJobType.text = job.jobType
        binding.lblWorkplace.text = job.workplace
        binding.lblSalary.text = "RM ${job.minSalary} - RM ${job.maxSalary} per month"
        binding.lblQualification.text = job.qualification
        binding.lblExperience.text = "${job.experience} year(s)"

        when (position) {
            0 -> {
                binding.tabDesc.visibility = View.VISIBLE
                binding.tabCompany.visibility = View.GONE
            }
            1 -> {
                binding.tabCompany.visibility = View.VISIBLE
                binding.tabDesc.visibility = View.GONE
            }
        }
    }



}