package com.example.launchpad.job_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentApplicantDetailsBinding
import com.example.launchpad.util.toBitmap
import com.example.launchpad.util.toast
import com.example.launchpad.viewmodel.ApplicantDetailsViewModel
import com.google.firebase.database.FirebaseDatabase
import io.getstream.avatarview.coil.loadImage

class ApplicantDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ApplicantDetailsFragment()
    }

    private val userVM: UserViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private lateinit var binding: FragmentApplicantDetailsBinding
    private val nav by lazy { findNavController() }
    private val jobAppID by lazy { arguments?.getString("jobAppID") ?: "" }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplicantDetailsBinding.inflate(inflater, container, false)

        binding.topAppBar.setNavigationOnClickListener { nav.navigateUp() }

        jobAppVM.getJobAppLD().observe(viewLifecycleOwner) {
            var jobApp = jobAppVM.get(jobAppID)
            if (jobApp == null) {
                nav.navigateUp()
                toast("Applicant Data Is Empty!")
                return@observe
            }

            jobApp.user = userVM.get(jobApp.userId)!!

            binding.applicantName.text = jobApp.user.name
            binding.avatarView.loadImage(jobApp.user.avatar.toBitmap())
            binding.fileName.text = jobApp.file.name
            binding.information.text = if (jobApp.info == "") "-" else jobApp.info

            binding.file.setOnClickListener {
                nav.navigate(
                    R.id.pdfViewerFragment, bundleOf(
                        "fileName" to jobApp.file.name,
                        "url" to jobApp.file.path
                    )
                )
            }


            //TODO chat
            binding.btnMessage.setOnClickListener {
                //the ids
                val chatRoomId = userVM.getAuth().uid + "_" + jobApp.userId
                createChatroom(chatRoomId)
                message(chatRoomId)
            }


        }


        return binding.root
    }

    fun createChatroom(chatRoomId: String) {
        val chatRoomsRef = FirebaseDatabase.getInstance().getReference("chatRooms")
        chatRoomsRef.child(chatRoomId).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                chatRoomsRef.child(chatRoomId).setValue(true)
            }
        }
    }

    private fun message(chatRoomId: String) {
        nav.navigate(
            R.id.chatTextFragment, bundleOf(
                "chatRoomId" to chatRoomId
            )
        )
    }
}