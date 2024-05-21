package com.example.launchpad.community.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.community.viewmodel.PostCommentViewModel
import com.example.launchpad.R
import com.example.launchpad.community.adapter.CommentAdapter
import com.example.launchpad.community.adapter.PostAdapter
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Post
import com.example.launchpad.data.PostComments
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentAddPostBinding
import com.example.launchpad.databinding.FragmentPostCommentBinding
import com.example.launchpad.util.cropToBlob
import com.example.launchpad.util.dialog
import com.example.launchpad.util.snackbar
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class PostCommentFragment : Fragment() {

    companion object {
        fun newInstance() = PostCommentFragment()
    }

    private lateinit var adapter: CommentAdapter
    private val postVM: PostViewModel by activityViewModels()
    private val postCommentsVM: PostCommentViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentPostCommentBinding
    private val postID by lazy { requireArguments().getString("postID", "") }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostCommentBinding.inflate(inflater, container, false)


        binding.topAppBar.setOnClickListener{
            findNavController().navigateUp()
        }
        binding.btnSend.setOnClickListener {
            togglePost(postID)
        }

        adapter = CommentAdapter { holder, post ->
            holder.binding.avatarView.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_postCommentFragment_to_userProfileFragment, bundleOf(
                            "userID" to post.userID
                        )
                    )

            }

        }

        binding.postResult.adapter = adapter

        postCommentsVM.getCommentsByPostID(postID).observe(viewLifecycleOwner) { commentList ->
            userVM.getUserLLD().observe(viewLifecycleOwner) { user ->
                commentList.forEach{ comment ->
                    comment.user = userVM.get(comment.userID) ?: User()
                }
            }
            val sortedCommentList = commentList.sortedByDescending { it.createdAt }

            adapter.submitList(sortedCommentList)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                adapter.notifyDataSetChanged()
            }, 100)

        }

        binding.refresh.setOnRefreshListener {
            adapter.notifyDataSetChanged()
            binding.refresh.isRefreshing = false
        }
        return binding.root
    }

    private fun togglePost(postID: String){
        postComment()
        addCommentsTotaltoPost(postID)
    }
    private fun postComment(){
        val comment = createCommentObject()

        if (!isPostValid(comment)) {
            snackbar("Please fulfill the requirement.")
            return
        }



        dialog("Comment", "Are you sure want to post this comment ?",
            onPositiveClick = { _, _ ->
                lifecycleScope.launch {
                    postCommentsVM.set(comment)

                }

                snackbar("Commented Successfully.")
                nav.navigateUp()


            }
        )
    }

    private fun createCommentObject(): PostComments {
        return PostComments(
            postID = postID ,
            createdAt = DateTime.now().millis,
            userID = userVM.getUserLD().value!!.uid,
            comment = binding.edtComment.text.toString().trim()
        )

    }

    private fun addCommentsTotaltoPost(postID : String){
        val tempPost = postVM.get(postID)
        val f = tempPost?.let {
            Post(
                postID= it.postID,
                description= it.description,
                image= it.image,
                createdAt= it.createdAt,
                userID= it.userID,
                comments= it.comments+1,
                likes= it.likes
            )
        }

        if (f != null) {
            postVM.update(f)
        }
    }

    private fun isPostValid(comments: PostComments): Boolean {

        val validation = postCommentsVM.validateInput(binding.txtCommenting,comments.comment )

        return validation
    }


}