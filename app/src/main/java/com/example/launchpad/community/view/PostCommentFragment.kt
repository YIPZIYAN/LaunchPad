package com.example.launchpad.community.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.community.viewmodel.PostCommentViewModel
import com.example.launchpad.R
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Post
import com.example.launchpad.data.PostComments
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
            postComment()
        }
        return binding.root
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

                snackbar("Commentted Successfully.")
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

    private fun isPostValid(comments: PostComments): Boolean {

        val validation = postCommentsVM.validateInput(binding.txtCommenting,comments.comment )

        return validation
    }
}