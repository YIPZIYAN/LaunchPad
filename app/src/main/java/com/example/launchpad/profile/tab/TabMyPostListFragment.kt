package com.example.launchpad.profile.tab

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.community.adapter.PostAdapter
import com.example.launchpad.community.viewmodel.PostLikesViewModel
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Post
import com.example.launchpad.data.PostLikes
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentTabMyPostListBinding
import com.example.launchpad.profile.adapter.PostPhotoAdapter
import com.example.launchpad.util.dialog
import com.example.launchpad.util.snackbar
import com.example.launchpad.viewmodel.CommunityViewModel
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import kotlin.properties.Delegates

class TabMyPostListFragment(val userID: String = "") : Fragment() {

    private lateinit var adapter: PostPhotoAdapter
    private val postVM: PostViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var binding: FragmentTabMyPostListBinding
    private val nav by lazy { findNavController() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabMyPostListBinding.inflate(inflater, container, false)

        adapter = PostPhotoAdapter{holder, post ->
            holder.binding.imageView.setOnClickListener {
                findNavController().navigate(
                    R.id.postDetailsFragment, bundleOf(
                        "postID" to post.postID
                    )
                )
            }
        }
        binding.rv.adapter = adapter

        postVM.getPostLD().observe(viewLifecycleOwner) { postList ->
            if (postList.isEmpty()) {
                return@observe
            }

            // if no id pass in (My Post), use own id
            val uid = if (userID == "") userVM.getAuth().uid else userID

            var sortedPostList =
                postList.filter { it.deletedAt == 0L }
                    .filter { it.userID == uid }
                    .sortedByDescending { it.createdAt }

            if (sortedPostList.isEmpty()){
                return@observe
            }

            adapter.submitList(sortedPostList)
        }

        return binding.root
    }

}