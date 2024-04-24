package com.example.launchpad

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.launchpad.auth.viewmodel.SignUpViewModel
import com.example.launchpad.databinding.ActivityEmailVerificationBinding
import com.example.launchpad.util.intentWithoutBackstack

class EmailVerificationActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmailVerificationBinding
    val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkEmailVerificationInterval()

        binding.txtSentTo.text = """
            An email verification sent to 
            ${viewModel.getCurrentUser()?.email}
        """.trimIndent()

        viewModel.isVerified.observe(this) {
            if (it) intentWithoutBackstack(this, UserActivity::class.java)
        }
    }
}