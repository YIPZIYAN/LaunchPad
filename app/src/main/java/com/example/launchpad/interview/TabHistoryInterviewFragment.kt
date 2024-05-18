package com.example.launchpad.interview

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentTabHistoryInterviewBinding

import com.example.launchpad.viewmodel.TabPendingInterviewViewModel

class TabHistoryInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = TabHistoryInterviewFragment()
    }

    private val viewModel: TabPendingInterviewViewModel by viewModels()
    private lateinit var binding: FragmentTabHistoryInterviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentTabHistoryInterviewBinding.inflate(inflater, container, false)


        return binding.root
    }
}