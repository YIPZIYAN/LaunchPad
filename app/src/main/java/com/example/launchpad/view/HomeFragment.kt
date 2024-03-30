package com.example.launchpad.view

import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.viewmodel.HomeViewModel
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentHomeBinding
import com.example.launchpad.databinding.FragmentMyProfileBinding
import com.example.launchpad.view.LoginFragment.Companion.userType

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.jobCard.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_jobDetailsFragment)
        }

        binding.btnPostJob.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_postJobFragment)
        }

        if (userType == 0) {
            binding.homeTitle.text = resources.getString(R.string.your_posted_job)
            binding.bookmark.visibility = View.GONE
            binding.btnPostJob.visibility = View.VISIBLE
            binding.jobCard2.visibility = View.GONE
            binding.jobCard3.visibility = View.GONE
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}