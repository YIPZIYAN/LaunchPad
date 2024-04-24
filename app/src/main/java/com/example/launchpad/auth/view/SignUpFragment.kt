package com.example.launchpad.auth.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentSignUpBinding
import com.example.launchpad.auth.viewmodel.SignUpViewModel
import com.example.launchpad.util.displayErrorHelper
import com.example.launchpad.util.toast
import java.util.regex.Pattern

class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.btnSignUp.setOnClickListener { submit() }

        viewModel.responseMsg.observe(viewLifecycleOwner) { toast(it) }

        return binding.root
    }

    private fun submit() {
        resetError()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()
        val passwordConfirmation = binding.edtPasswordConfirmation.text.toString()

        if (!isValid(email, password, passwordConfirmation)) {
            return
        }

        viewModel.signUpWithEmail(email, password)
    }

    private fun isValid(email: String, password: String, passwordConfirmation: String): Boolean {
        when {

            email == "" || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                displayErrorHelper(binding.lblEmail, "Invalid email address.")
                return false
            }

            password.length < 8 || !password.any { it.isLetter() } || !password.any { it.isDigit() } -> {
                displayErrorHelper(
                    binding.lblPassword,
                    "Password does not fulfill the criteria.\n" + getString(R.string.password_helper_text)
                )
                return false
            }

            password != passwordConfirmation -> {
                displayErrorHelper(binding.lblPasswordConfirm, "Passwords do not match.")
                return false
            }

            else -> return true
        }
    }

    private fun resetError() {
        val edt = listOf(
            binding.lblPassword,
            binding.lblEmail,
            binding.lblPasswordConfirm
        ).forEach { it.error = null }
    }

}