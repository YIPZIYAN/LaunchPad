package com.example.launchpad.profile.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentMyProfileBinding
import com.example.launchpad.view.TabMyJobFragment
import com.example.launchpad.profile.viewmodel.MyProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import io.getstream.avatarview.coil.loadImage

class MyProfileFragment : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private val userVM: UserViewModel by viewModels()
    private lateinit var binding: FragmentMyProfileBinding
    private val tabItems = arrayOf(
        "Job",
        "Post"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)

        userVM.getUserLD().observe(viewLifecycleOwner) { user ->
            binding.txtName.text = user.name
            binding.avatarView.loadImage(user.avatar)
        }

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.setting -> {
                    findNavController().navigate(R.id.action_profileFragment_to_settingFragment)
                    true
                }

                else -> false
            }
        }

        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabItems[position]
        }.attach()
        return binding.root
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return TabMyJobFragment()
                1 -> return TabMyJobFragment()
                else -> throw Exception()
            }
        }
    }


}