package com.example.launchpad.profile.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentMyProfileBinding
import com.example.launchpad.databinding.FragmentUserProfileBinding
import com.example.launchpad.profile.TabMyJobFragment
import com.example.launchpad.profile.TabMyPostListFragment
import com.example.launchpad.util.toBitmap
import com.google.android.material.tabs.TabLayoutMediator
import io.getstream.avatarview.coil.loadImage

class UserProfileFragment : Fragment() {

    companion object {
        fun newInstance() = UserProfileFragment()
    }

    private val userVM: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentUserProfileBinding
    private val tabItems = arrayOf(
        "Job",
        "Post"
    )
    private val userID by lazy { requireArguments().getString("userID", "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        userVM.getUserLLD().observe(viewLifecycleOwner) { userList ->
            val user = userList.find { it.uid == userID }
            user?.let {
                val avatar =
                    if (it.avatar.toBytes().isEmpty())
                        R.drawable.round_account_circle_24
                    else
                        it.avatar.toBitmap()

                binding.txtName.text = it.name
                binding.avatarView.loadImage(avatar)

            }
        }
        if (userVM.getUserLD().value!!.uid == userID) {
            binding.btnMessage.visibility = View.INVISIBLE
        }


        binding.topAppBar.setOnClickListener{
            findNavController().navigateUp()
        }

        val adapter =
            UserProfileFragment.ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle,userID)
        binding.viewPager.adapter = adapter


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabItems[position]
        }.attach()
        return binding.root
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,private val userID: String) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> return TabMyJobFragment()
                1 -> return TabMyPostListFragment.newInstance(userID,false)
                else -> throw Exception()
            }
        }
}
}
