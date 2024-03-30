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
import com.example.launchpad.databinding.FragmentViewApplicantBinding
import com.example.launchpad.viewmodel.ViewApplicantViewModel

class ViewApplicantFragment : Fragment() {

    companion object {
        fun newInstance() = ViewApplicantFragment()
    }

    private val viewModel: ViewApplicantViewModel by viewModels()
    private lateinit var binding: FragmentViewApplicantBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_applicant, container, false)

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = MyPagerAdapter(childFragmentManager)
        binding.tabContent.adapter = adapter
        binding.tab.setupWithViewPager(binding.tabContent)
    }

    class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TabApplicantFragment.newInstance(0)
                1 -> TabApplicantFragment.newInstance(1)
                2 -> TabApplicantFragment.newInstance(2)
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }

        override fun getCount(): Int {
            return 3  // Number of tabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "New"
                1 -> "Accepted"
                2 -> "Rejected"
                else -> null
            }
        }
    }
}