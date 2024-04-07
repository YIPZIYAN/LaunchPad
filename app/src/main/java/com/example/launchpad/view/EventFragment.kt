package com.example.launchpad.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.launchpad.viewmodel.EventViewModel
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentEventBinding

class EventFragment : Fragment() {

    companion object {
        fun newInstance() = EventFragment()
    }

    private lateinit var viewModel: EventViewModel
    private lateinit var binding: FragmentEventBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event, container, false)
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
                0 -> TabPendingInterviewFragment()
                1 -> TabPendingInterviewFragment()
                2 -> TabPendingInterviewFragment()
                else -> throw Exception()
            }
        }

        override fun getCount(): Int {
            return 3  // Number of tabs
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Pending"
                1 -> "Upcoming"
                2 -> "History"
                else -> null
            }
        }
    }

}