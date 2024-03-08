package com.example.launchpad.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.user_nav_host)!!.findNavController() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)

        binding.bottomNavigation.setupWithNavController(nav)
        setContentView(binding.root)
    }
}