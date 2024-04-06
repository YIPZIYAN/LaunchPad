package com.example.launchpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.launchpad.R
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
            Handler(Looper.getMainLooper()).postDelayed({
                when (destination.id) {
                    R.id.jobDetailsFragment,
                    R.id.applyJobFragment,
                    R.id.postJobFragment,
                    R.id.viewApplicantFragment,
                    R.id.applicantDetailsFragment,
                    R.id.chatTextFragment,
                    R.id.settingFragment,
                    R.id.addPostFragment,
                    R.id.userProfileFragment,
                    -> {
                        binding.bottomNavigation.visibility = View.GONE
                    }

                    else -> {
                        binding.bottomNavigation.visibility = View.VISIBLE
                    }
                }
            }, 200)
        }
        binding.bottomNavigation.setupWithNavController(nav)
    }
}