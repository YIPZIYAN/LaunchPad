package com.example.launchpad.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.viewmodel.CommunityViewModel
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentCommunityBinding
import com.example.launchpad.databinding.FragmentMyProfileBinding
import io.getstream.avatarview.coil.loadImage


class CommunityFragment : Fragment() {

    companion object {
        fun newInstance() = CommunityFragment()
    }

    private lateinit var viewModel: CommunityViewModel
    private lateinit var binding: FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)


        binding.btnAddPost.setOnClickListener{
            findNavController().navigate(R.id.action_communityFragment_to_addPostFragment)
        }

        binding.imageView.setOnClickListener{
            findNavController().navigate(R.id.action_communityFragment_to_userProfileFragment)
        }

        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        // TODO: Use the ViewModel
    }

}