package com.example.launchpad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.launchpad.auth.viewmodel.SignUpViewModel
import com.example.launchpad.databinding.ActivityEmailVerificationBinding

class EmailVerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailVerificationBinding
    val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.

    }
}