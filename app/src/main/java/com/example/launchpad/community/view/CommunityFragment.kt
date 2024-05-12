package com.example.launchpad.community.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.viewmodel.CommunityViewModel
import com.example.launchpad.R
import com.example.launchpad.community.adapter.PostAdapter
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Company
import com.example.launchpad.data.SaveJob
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentCommunityBinding
import com.example.launchpad.job.adapter.JobAdapter


class CommunityFragment : Fragment() {

    companion object {
        fun newInstance() = CommunityFragment()
    }

    private lateinit var adapter: PostAdapter
    private val postVM: PostViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
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



        adapter = PostAdapter { holder, post ->
            holder.binding.imageView.setOnClickListener{
                findNavController().navigate(R.id.action_communityFragment_to_userProfileFragment)
            }
        }

        binding.postResult.adapter = adapter


        postVM.getPostLD().observe(viewLifecycleOwner) { postList ->
            userVM.getUserLLD().observe(viewLifecycleOwner) { user ->
                    postList.forEach{ post ->
                        post.user = userVM.get(post.userID) ?: User()
                    }
            }

            adapter.submitList(postList)
        }





    //-----------------------------------------------------------
    // Refresh
    binding.refresh.setOnRefreshListener {
        adapter.notifyDataSetChanged()
        binding.refresh.isRefreshing = false
    }


        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        // TODO: Use the ViewModel
    }

}