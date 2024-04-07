package com.example.launchpad.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentTabPendingInterviewBinding
import com.example.launchpad.viewmodel.TabPendingInterviewViewModel

class TabPendingInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = TabPendingInterviewFragment()
    }

    private val viewModel: TabPendingInterviewViewModel by viewModels()
    private lateinit var binding: FragmentTabPendingInterviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tab_pending_interview, container, false)
        binding.applicant.setOnClickListener {
            findNavController().navigate(R.id.action_eventFragment_to_scheduleInterviewFragment)
        }

        return binding.root
    }
}