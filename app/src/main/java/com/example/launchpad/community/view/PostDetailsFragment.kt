package com.example.launchpad.community.view

import android.graphics.drawable.Drawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.navigation.fragment.navArgs
import com.example.launchpad.R
import com.example.launchpad.community.adapter.CommentAdapter
import com.example.launchpad.community.adapter.PostAdapter
import com.example.launchpad.community.viewmodel.PostCommentViewModel
import com.example.launchpad.community.viewmodel.PostLikesViewModel
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Post
import com.example.launchpad.data.PostLikes
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentCommunityBinding
import com.example.launchpad.databinding.FragmentPostDetailsBinding
import com.example.launchpad.util.dialog
import com.example.launchpad.util.displayPostTime
import com.example.launchpad.util.setImageBlob
import com.example.launchpad.util.snackbar
import com.example.launchpad.viewmodel.CommunityViewModel
import com.example.launchpad.viewmodel.PostDetailsViewModel
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class PostDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = PostDetailsFragment()
    }


    private lateinit var adapter: PostAdapter
    private lateinit var adapterComment: CommentAdapter
    private val postVM: PostViewModel by activityViewModels()
    private val postLikesVM: PostLikesViewModel by activityViewModels()
    private val postCommentsVM: PostCommentViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var viewModel: PostDetailsViewModel
    private lateinit var binding: FragmentPostDetailsBinding
    private val postID by lazy { requireArguments().getString("postID", "") }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDetailsBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnClickListener{
            findNavController().navigateUp()
        }

        adapterComment = CommentAdapter { holder, post ->
            holder.binding.avatarView.setOnClickListener {
                findNavController().navigate(
                    R.id.action_postCommentFragment_to_userProfileFragment, bundleOf(
                        "userID" to post.userID
                    )
                )

            }

        }

       var targetPost = postVM.get(postID)

        if (targetPost != null) {

            var targetUser = userVM.get(targetPost.userID)
            if (targetUser != null) {
                binding.avatarView.setImageBlob(targetUser.avatar)
                binding.txtUsername.text = targetUser.name
            }
            binding.postImageView.setImageBlob(targetPost.image)
            binding.txtDescription.text = targetPost.description

            binding.txtTime.text = displayPostTime(targetPost.createdAt)
            binding.txtLikes.text = targetPost.likes.toString()
            binding.txtComments.text = targetPost.comments.toString() + " Comments"

            binding.btnLike.setOnClickListener {
                toggleLike(targetPost)
            }

            binding.avatarView.setOnClickListener {
                findNavController().navigate(
                    R.id.action_communityFragment_to_userProfileFragment, bundleOf(
                        "userID" to targetPost.userID
                    )
                )
            }
            if(targetPost.userID != userVM.getUserLD().value!!.uid){
                binding.btnMoreOptions.visibility = View.INVISIBLE
            }

            binding.btnMoreOptions.setOnClickListener {
                showPopupMenu(binding.btnMoreOptions, targetPost.postID) }

            updateThumbUpDrawable(targetPost)

        }

        binding.commentResult.adapter = adapterComment


        postCommentsVM.getCommentsByPostID(postID).observe(viewLifecycleOwner) { commentList ->
            userVM.getUserLLD().observe(viewLifecycleOwner) { user ->
                commentList.forEach{ comment ->
                    comment.user = userVM.get(comment.userID) ?: User()
                }
            }
            val sortedCommentList = commentList.sortedByDescending { it.createdAt }

            adapterComment.submitList(sortedCommentList)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                adapterComment.notifyDataSetChanged()
            }, 100)

        }

        // Refresh
        binding.refresh.setOnRefreshListener {

                adapterComment.notifyDataSetChanged()
                binding.refresh.isRefreshing = false
            }


        return binding.root
    }

    private fun toggleLike(post: Post) {
        val userId = userVM.getUserLD().value!!.uid
        if (postLikesVM.get(post.postID, userId) != null) {
            postLikesVM.delete(post.postID, userId)
            removeLikesTotaltoPost(post.postID)

        } else {
            postLikesVM.set(addPostlikes(post.postID))
            addLikesTotaltoPost(post.postID)
        }
    }

    private fun addPostlikes(postID : String): PostLikes {
        return PostLikes(
            postLikesID = "",
            postID = postID,
            userID = userVM.getUserLD().value!!.uid
        )
    }
    private fun addLikesTotaltoPost(postID : String){
        val tempPost = postVM.get(postID)
        val f = tempPost?.let {
            Post(
                postID= it.postID,
                description= it.description,
                image= it.image,
                createdAt= it.createdAt,
                userID= it.userID,
                comments= it.comments,
                likes= it.likes +1
            )
        }

        if (f != null) {
            postVM.update(f)
        }
    }

    private fun removeLikesTotaltoPost(postID : String){
        val tempPost = postVM.get(postID)
        val f = tempPost?.let {
            Post(
                postID= it.postID,
                description= it.description,
                image= it.image,
                createdAt= it.createdAt,
                userID= it.userID,
                comments= it.comments,
                likes= it.likes -1
            )
        }

        if (f != null) {
            postVM.update(f)
        }
    }

    private fun updateThumbUpDrawable(post: Post) {
        val userId = userVM.getUserLD().value!!.uid
        val isLiked = postLikesVM.get(post.postID, userId) != null
        val thumbUpDrawableRes = if (isLiked) {
            R.drawable.baseline_thumb_up_24_blue
        } else {
            R.drawable.baseline_thumb_up_off_alt_24_blue
        }
        val drawable = ContextCompat.getDrawable(binding.root.context, thumbUpDrawableRes)
        binding.imageThumbUp.setImageDrawable(drawable)
    }

    fun showPopupMenu(view: View,postID: String) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.post_menu)

        // Set up a listener for menu item clicks
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_edit -> {
                    // Handle edit action
                    findNavController().navigate(R.id.action_postDetailsFragment_to_addPostFragment, bundleOf(
                        "postID" to postID
                    ))
                    true
                }
                R.id.menu_delete -> {
                    // Handle delete action
                    val oriPost = postVM.get(postID)
                    if(oriPost!=null){
                        val updatedPost = oriPost.copy(deletedAt = DateTime.now().millis)
                        dialog("Delete Post", "Are you sure want to delete this post ?",
                            onPositiveClick = { _, _ ->
                                lifecycleScope.launch {
                                    postVM.update(updatedPost)
                                }
                                snackbar("Post Deleted Successfully.")
                            })
                    }
                    true
                }
                else -> false
            }
        }
        // Show the popup menu
        popupMenu.show()
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PostDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }
}