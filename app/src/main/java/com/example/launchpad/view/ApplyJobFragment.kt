package com.example.launchpad.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentApplyJobBinding
import com.example.launchpad.databinding.FragmentHomeBinding
import com.example.launchpad.viewmodel.ApplyJobViewModel
import com.example.launchpad.viewmodel.HomeViewModel
import com.example.launchpad.viewmodel.PostJobViewModel

class ApplyJobFragment : Fragment() {

    companion object {
        fun newInstance() = ApplyJobFragment()
    }

    private lateinit var viewModel: ApplyJobViewModel
    private lateinit var binding: FragmentApplyJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyJobBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ApplyJobViewModel::class.java)
        // TODO: Use the ViewModel
    }
}