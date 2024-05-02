package com.example.launchpad

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.ActivityUserBinding
import com.example.launchpad.job.viewmodel.JobViewModel

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private val nav by lazy {
        supportFragmentManager.findFragmentById(R.id.user_nav_host)!!.findNavController()
    }
    private val jobVM: JobViewModel by viewModels()
    private val companyVM: CompanyViewModel by viewModels()
    private val userVM: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //Early data loading
        userVM.init()
        companyVM.init()
        Log.d("USER IS GOOGLE?", " ${userVM.isGoogleLogin()}")



        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setupNav()
        setContentView(binding.root)
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
                R.id.signUpEnterpriseFragment
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
}