package com.example.launchpad.job_application

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentApplicantDetailsBinding
import com.example.launchpad.util.JobApplicationState
import com.example.launchpad.util.dialog
import com.example.launchpad.util.displayPostTime
import com.example.launchpad.util.snackbar
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
    private val jobAppVM: JobApplicationViewModel by viewModels()
    private lateinit var binding: FragmentApplicantDetailsBinding
    private val nav by lazy { findNavController() }
    private val jobAppID by lazy { arguments?.getString("jobAppID") ?: "" }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplicantDetailsBinding.inflate(inflater, container, false)

        jobAppVM.response.observe(viewLifecycleOwner) { if (it != null) toast(it) }
        jobAppVM.isSuccess.observe(viewLifecycleOwner) { if (it) snackbar("Job Application Status Updated!") }

        binding.topAppBar.setNavigationOnClickListener { nav.navigateUp() }

        jobAppVM.getJobAppLD().observe(viewLifecycleOwner) {
            var jobApp = jobAppVM.get(jobAppID)
            if (jobApp == null) {
                nav.navigateUp()
                toast("Applicant Data Is Empty!")
                return@observe
            }

            jobApp.user = userVM.get(jobApp.userId)!!

            /*
            * Load Applicant Data
            *
            * Load Applicant Data
            * =======================================
            *
            * */
            binding.applicantName.text = jobApp.user.name
            binding.avatarView.loadImage(jobApp.user.avatar.toBitmap())
            binding.fileName.text = jobApp.file.name
            binding.information.text = if (jobApp.info == "") "-" else jobApp.info
            binding.appliedDate.text = "Applied " + displayPostTime(jobApp.createdAt)

            binding.file.setOnClickListener {
                nav.navigate(
                    R.id.pdfViewerFragment, bundleOf(
                        "fileName" to jobApp.file.name,
                        "url" to jobApp.file.path
                    )
                )
            }

            /*
            * Button Function
            * =======================================
            * */
            when (jobApp.status) {
                JobApplicationState.ACCEPTED.toString() -> {
                    binding.btnAccept.let {
                        it.isClickable = false
                        it.isEnabled = false
                        it.text = jobApp.status
                    }
                    binding.btnInterview.visibility = View.VISIBLE
                    binding.btnInterview.setOnClickListener { nav.navigate(R.id.scheduleInterviewFragment) }
                    binding.btnReject.visibility = View.GONE
                }

                JobApplicationState.REJECTED.toString() -> {
                    binding.btnReject.let {
                        it.isClickable = false
                        it.isEnabled = false
                        it.text = jobApp.status
                    }
                    binding.btnAccept.visibility = View.GONE
                }
            }

            binding.btnAccept.setOnClickListener {
                dialog("Accept Applicant", "Are you sure to ACCEPT ${jobApp.user.name} ?",
                    onPositiveClick = { _, _ ->
                        jobAppVM.updateStatus(
                            JobApplicationState.ACCEPTED,
                            jobAppID
                        )
                    })
            }
            binding.btnReject.setOnClickListener {
                dialog("Reject Applicant", "Are you sure to REJECT ${jobApp.user.name} ?",
                    onPositiveClick = { _, _ ->
                        jobAppVM.updateStatus(
                            JobApplicationState.REJECTED,
                            jobAppID
                        )
                    })

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