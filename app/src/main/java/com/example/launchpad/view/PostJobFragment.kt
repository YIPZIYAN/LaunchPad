package com.example.launchpad.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentMyProfileBinding
import com.example.launchpad.databinding.FragmentPostJobBinding
import com.example.launchpad.viewmodel.MyProfileViewModel
import com.example.launchpad.viewmodel.PostJobViewModel

class PostJobFragment : Fragment() {

    companion object {
        fun newInstance() = PostJobFragment()
    }

    private lateinit var viewModel: PostJobViewModel
    private lateinit var binding: FragmentPostJobBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPostJobBinding.inflate(inflater, container, false)
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostJobViewModel::class.java)
        // TODO: Use the ViewModel
    }
}