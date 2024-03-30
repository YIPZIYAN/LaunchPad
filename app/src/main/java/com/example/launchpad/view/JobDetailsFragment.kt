package com.example.launchpad.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentJobDetailsBinding
import com.example.launchpad.view.LoginFragment.Companion.userType
import com.example.launchpad.viewmodel.JobDetailsViewModel

class JobDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = JobDetailsFragment()
    }

    private val viewModel: JobDetailsViewModel by viewModels()
    private lateinit var binding: FragmentJobDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_job_details, container, false)

        if (userType == 0) {
            binding.topAppBar.menu.findItem(R.id.edit).setVisible(true)
            binding.topAppBar.menu.findItem(R.id.archive).setVisible(true)
            binding.btnApply.text = resources.getString(R.string.VIEW_APPLICANT)
            binding.btnApply.setOnClickListener {
                findNavController().navigate(R.id.action_jobDetailsFragment_to_viewApplicantFragment)
            }
        }
        else {
            binding.btnApply.setOnClickListener {
                findNavController().navigate(R.id.action_jobDetailsFragment_to_applyJobFragment)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MyPagerAdapter(childFragmentManager)
        binding.tabContent.adapter = adapter
        binding.tab.setupWithViewPager(binding.tabContent)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    findNavController().navigate(R.id.action_jobDetailsFragment_to_postJobFragment)
                    true
                }
                R.id.archive -> {

                    true
                }
                else -> false
            }
        }

    }

    class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TabJobDetailsFragment.newInstance(0)
                1 -> TabJobDetailsFragment.newInstance(1)
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

