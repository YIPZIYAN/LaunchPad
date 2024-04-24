package com.example.launchpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.launchpad.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val nav by lazy {
        supportFragmentManager.findFragmentById(R.id.user_nav_host)!!.findNavController()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
                R.id.scheduleInterviewFragment
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