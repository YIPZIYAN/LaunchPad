package com.example.launchpad.profile

import android.graphics.drawable.Drawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.community.adapter.PostAdapter
import com.example.launchpad.community.viewmodel.PostLikesViewModel
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Post
import com.example.launchpad.data.PostLikes
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentCommunityBinding
import com.example.launchpad.databinding.FragmentTabMyPostListBinding
import com.example.launchpad.viewmodel.CommunityViewModel
import com.example.launchpad.viewmodel.TabMyPostListViewModel
import kotlin.properties.Delegates

class TabMyPostListFragment : Fragment() {

    companion object {
        private const val ARG_USER_ID = "userID"
        private const val ARG_OWN_OR_OTHERS = "ownOrOthers" // Changed the key to a unique name
        fun newInstance(userID: String, from: Boolean): TabMyPostListFragment {
            val fragment = TabMyPostListFragment()
            val args = Bundle()
            args.putString(ARG_USER_ID, userID)
            args.putBoolean(ARG_OWN_OR_OTHERS, from) // Use the correct key for the boolean
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var userID: String
    private var from by Delegates.notNull<Boolean>()
    private lateinit var adapter: PostAdapter
    private val postVM: PostViewModel by activityViewModels()
    private val postLikesVM: PostLikesViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var viewModel: CommunityViewModel
    private lateinit var binding: FragmentTabMyPostListBinding
    private var isSearching = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userID = it.getString(ARG_USER_ID) ?: ""
            from = it.getBoolean(ARG_OWN_OR_OTHERS.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabMyPostListBinding.inflate(inflater, container, false)

        adapter = PostAdapter { holder, post ->

            holder.binding.btnLike.setOnClickListener {
                toggleLike(post)
            }
            if(from){
                holder.binding.txtComments.setOnClickListener {
                    findNavController().navigate(R.id.action_profileFragment_to_postCommentFragment, bundleOf(
                        "postID" to post.postID
                    ))
                }
                holder.binding.btnComment.setOnClickListener {
                    findNavController().navigate(R.id.action_profileFragment_to_postCommentFragment, bundleOf(
                        "postID" to post.postID
                    ))
                }

            }else{
                holder.binding.txtComments.setOnClickListener {
                    findNavController().navigate(R.id.action_userProfileFragment_to_postCommentFragment, bundleOf(
                        "postID" to post.postID
                    ))
                }
                holder.binding.btnComment.setOnClickListener {
                    findNavController().navigate(R.id.action_userProfileFragment_to_postCommentFragment, bundleOf(
                        "postID" to post.postID
                    ))
                }
            }


            updateThumbUpDrawable(holder, post)
        }

        binding.rv.adapter = adapter

        postVM.getPostLD().observe(viewLifecycleOwner) { postList ->
            val userPosts = postList.filter { it.userID == userID }
            userVM.getUserLLD().observe(viewLifecycleOwner) { userList ->
                userPosts.forEach { post ->
                    val user = userList.find { it.uid == post.userID } ?: User()
                    post.user = user
                }

                val sortedPostList = userPosts.sortedByDescending { it.createdAt }
                adapter.submitList(sortedPostList)

                // Notify the adapter with a delay
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    adapter.notifyDataSetChanged()
                }, 100)
            }
        }

        return binding.root
    }

    private fun toggleLike(post: Post) {
        val userId = userVM.getUserLD().value!!.uid
        if (postLikesVM.get(post.postID, userId) != null) {
            postLikesVM.delete(post.postID, userId)
            removeLikesTotaltoPost(post.postID)
            adapter.notifyDataSetChanged()
        } else {
            postLikesVM.set(addPostlikes(post.postID))
            addLikesTotaltoPost(post.postID)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addPostlikes(postID: String): PostLikes {
        return PostLikes(
            postLikesID = "",
            postID = postID,
            userID = userVM.getUserLD().value!!.uid
        )
    }

    private fun addLikesTotaltoPost(postID: String) {
        val tempPost = postVM.get(postID)
        val f = tempPost?.let {
            Post(
                postID = it.postID,
                description = it.description,
                image = it.image,
                createdAt = it.createdAt,
                userID = it.userID,
                comments = it.comments,
                likes = it.likes + 1
            )
        }

        if (f != null) {
            postVM.update(f)
        }
    }

    private fun removeLikesTotaltoPost(postID: String) {
        val tempPost = postVM.get(postID)
        val f = tempPost?.let {
            Post(
                postID = it.postID,
                description = it.description,
                image = it.image,
                createdAt = it.createdAt,
                userID = it.userID,
                comments = it.comments,
                likes = it.likes - 1
            )
        }

        if (f != null) {
            postVM.update(f)
        }
    }

    private fun updateThumbUpDrawable(holder: PostAdapter.ViewHolder, post: Post) {
        val userId = userVM.getUserLD().value!!.uid
        val isLiked = postLikesVM.get(post.postID, userId) != null
        val thumbUpDrawableRes = if (isLiked) {
            R.drawable.baseline_thumb_up_24_blue
        } else {
            R.drawable.baseline_thumb_up_off_alt_24_blue
        }
        val thumbUpDrawable: Drawable? = ContextCompat.getDrawable(holder.itemView.context, thumbUpDrawableRes)
        holder.binding.imageThumbUp.setImageDrawable(thumbUpDrawable)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        // TODO: Use the ViewModel
    }
}