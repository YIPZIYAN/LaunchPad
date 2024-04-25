package com.example.launchpad.job.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentJobDetailsBinding
import com.example.launchpad.auth.view.LoginFragment.Companion.userType
import com.example.launchpad.data.Company
import com.example.launchpad.data.Job
import com.example.launchpad.profile.viewmodel.CompanyViewModel
import com.example.launchpad.util.setImageBlob
import com.example.launchpad.job.viewmodel.JobViewModel

class JobDetailsFragment : Fragment() {

    private lateinit var binding: FragmentJobDetailsBinding
    private val nav by lazy { findNavController() }
    private val jobVM: JobViewModel by activityViewModels()
    private val companyVM: CompanyViewModel by activityViewModels()

    private val jobID by lazy { arguments?.getString("jobID") ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.e("ONCREATEVIEW", "xxx")

        binding = FragmentJobDetailsBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            nav.navigateUp()
        }

        if (userType == 0) {
            binding.topAppBar.menu.findItem(R.id.edit).setVisible(true)
            binding.topAppBar.menu.findItem(R.id.archive).setVisible(true)
            binding.btnApply.text = resources.getString(R.string.VIEW_APPLICANT)
            binding.btnApply.setOnClickListener {
                nav.navigate(R.id.action_jobDetailsFragment_to_viewApplicantFragment)
            }
        }
        else {
            binding.btnApply.setOnClickListener {
                nav.navigate(R.id.action_jobDetailsFragment_to_applyJobFragment)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ONVIEWCREATED", "yyy")

        val job = jobVM.get(jobID)!!


        if (job == null) {
            nav.navigateUp()
            return
        }

        job.company = companyVM.get(job.companyID) ?: Company()

        binding.jobName.text = job.jobName
        binding.companyAvatar.setImageBlob(job.company.avatar)
        binding.companyName.text = job.company.name

        val adapter = MyPagerAdapter(childFragmentManager, job.jobID)
        binding.tabContent.adapter = adapter
        binding.tab.setupWithViewPager(binding.tabContent)
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    detail(job.jobID)
                    true
                }
                R.id.archive -> {

                    true
                }
                else -> false
            }
        }
        /*
        Handler(Looper.getMainLooper()).postDelayed({
            binding.loadingView.visibility = View.GONE
        }, 500)
        */
    }


    private fun detail(jobID: String) {
        nav.navigate(
            R.id.postJobFragment, bundleOf(
                "jobID" to jobID
            )
        )
    }

    class MyPagerAdapter(fm: FragmentManager, private val jobID: String) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {

            return when (position) {
                0 -> TabJobDetailsFragment.newInstance(0, jobID)
                1 -> TabJobDetailsFragment.newInstance(1, jobID)
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }

        override fun getCount(): Int {
            return 2  // Number of tabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Description"
                1 -> "Company"
                else -> null
            }
        }
    }

}

