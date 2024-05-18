package com.example.launchpad.community.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.community.viewmodel.PostViewModel
import com.example.launchpad.data.Post
import com.example.launchpad.data.PostComments
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentAddPostBinding
import com.example.launchpad.util.cropToBlob
import com.example.launchpad.util.dialog
import com.example.launchpad.util.snackbar
import kotlinx.coroutines.launch
import org.joda.time.DateTime

class AddPostFragment : Fragment() {

    companion object {
        fun newInstance() = AddPostFragment()
    }

    private val postVM: PostViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentAddPostBinding
    private val postID by lazy { arguments?.getString("postID") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)

        validateOnTextChanged()
        binding.btnUploadImage.setOnClickListener { select()}
        binding.btnPost.setOnClickListener { post() }

        binding.topAppBar.setOnClickListener{
            findNavController().navigateUp()
        }
        return binding.root
    }

    private fun validateOnTextChanged() {
        val textFields = listOf(
            binding.edtDescription

        )

        textFields.forEach { textField ->
            textField.doOnTextChanged { text, _, _, _ ->
                val label = when (textField) {
                    binding.edtDescription -> binding.lblPostDescription

                    else -> null
                }
                postVM.validateInput(label!!, text.toString().trim())
            }
        }


    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        binding.edtImage.setImageURI(it)
    }

    private fun select() {
        // Select file
        getContent.launch("image/*")
    }

    private fun post() {
        val post = createPostObject(false)

        if (!isPostValid(post)) {
            snackbar("Please fulfill the requirement.")
            return
        }

        if(!isImageValid()){
            snackbar("Please Add a picture.")
            return
        }

        dialog("Create Post", "Are you sure want to submit this post ?",
            onPositiveClick = { _, _ ->
                lifecycleScope.launch {
                    postVM.set(post)
                }

                snackbar("Post Submitted Successfully.")
                nav.navigateUp()

            }
        )
    }

    private fun createPostObject(isEditing: Boolean): Post {
        return Post(
            postID = if (isEditing) postID else "",
            description = binding.edtDescription.text.toString().trim(),
            createdAt = if (isEditing) binding.createdAt.text.toString()
                .toLong() else DateTime.now().millis,
            image = binding.edtImage.cropToBlob(binding.edtImage.getDrawable().getIntrinsicWidth(),binding.edtImage.getDrawable().getIntrinsicHeight()),
            userID = userVM.getUserLD().value!!.uid,
            comments = 0,
            likes = 0
        )

    }

    private fun isPostValid(post: Post): Boolean {

        val validation = postVM.validateInput(binding.lblPostDescription, post.description)


        return validation
    }


    private fun isImageValid(): Boolean {
        if(binding.edtImage.getDrawable() == null){
            return false
        }else
            return true

    }

}