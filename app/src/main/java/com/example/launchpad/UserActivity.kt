package com.example.launchpad

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.InterviewViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.ActivityUserBinding
import com.example.launchpad.job.viewmodel.JobViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private val nav by lazy {
        supportFragmentManager.findFragmentById(R.id.user_nav_host)!!.findNavController()
    }
    private val jobVM: JobViewModel by viewModels()
    private val jobAppVM: JobApplicationViewModel by viewModels()
    private val companyVM: CompanyViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()
    private val interviewVM: InterviewViewModel by viewModels()
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        //Early data loading
        userVM.init()
        companyVM.init()
        jobAppVM.init()
        interviewVM.init()


        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setupNav()
        setContentView(binding.root)

        // To prevent logout null pointer exception when onPause
        userId = userVM.getAuth().uid
        setOnlineStatus(userId, true)
    }

    private fun setupNav() {
        nav.addOnDestinationChangedListener { _, destination, _ ->

            val hideBottomNavDestinations = setOf(
                R.id.jobDetailsFragment,
                R.id.applyJobFragment,
                R.id.postJobFragment,
                R.id.viewApplicantFragment,
                R.id.applicantDetailsFragment,
                R.id.chatTextFragment,
                R.id.settingFragment,
                R.id.addPostFragment,
                R.id.userProfileFragment,
                R.id.scheduleInterviewFragment,
                R.id.savedJobFragment,
                R.id.archivedJobFragment,
                R.id.signUpEnterpriseFragment,
                R.id.emailVerificationFragment,
                R.id.profileUpdateFragment,
                R.id.pdfViewerFragment,
            )

            val isBottomNavVisible = !hideBottomNavDestinations.contains(destination.id)
            //TransitionManager.beginDelayedTransition(binding.root as ViewGroup)

            Handler(Looper.getMainLooper()).postDelayed({
                binding.bottomNavigation.visibility =
                    if (isBottomNavVisible) View.VISIBLE else View.GONE
            }, -100)

        }

        binding.bottomNavigation.setupWithNavController(nav)

    }

    fun setOnlineStatus(userId: String, isOnline: Boolean) {
        val onlineStatusRef = FirebaseDatabase.getInstance().getReference("onlineStatus")
        onlineStatusRef.child(userId).get().addOnSuccessListener { snapshot ->
            if (isOnline) {
                onlineStatusRef.child(userId).setValue(true)
            }
            else {
                onlineStatusRef.child(userId).setValue(false)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        setOnlineStatus(userId, true)
    }

    override fun onPause() {
        super.onPause()
        setOnlineStatus(userId, false)
    }


}