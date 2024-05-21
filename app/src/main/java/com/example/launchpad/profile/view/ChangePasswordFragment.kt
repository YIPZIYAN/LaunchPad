package com.example.launchpad.profile.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.MainActivity
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentChangePasswordBinding
import com.example.launchpad.databinding.FragmentLoginBinding
import com.example.launchpad.profile.viewmodel.ChangePasswordViewModel
import com.example.launchpad.util.dialog
import com.example.launchpad.util.displayErrorHelper
import com.example.launchpad.util.intentWithoutBackstack
import com.example.launchpad.util.snackbar
import com.example.launchpad.util.toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ChangePasswordFragment : Fragment() {

    private val nav by lazy { findNavController() }

    private lateinit var binding: FragmentChangePasswordBinding
    private val viewModel: ChangePasswordViewModel by viewModels()
    private var newPassword = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnClickListener { nav.navigateUp() }

        binding.btnChangePassword.setOnClickListener { changePassword() }

        viewModel.response.observe(viewLifecycleOwner) { if (it != null) toast(it) }
        viewModel.isCorrectPassword.observe(viewLifecycleOwner) {
            if (it) {
                dialog(
                    "Change Password",
                    getString(R.string.change_password_confirmation),
                    onPositiveClick = { _, _ ->
                        viewModel.resetPassword(newPassword)
                    }
                )
            }
        }
        viewModel.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                snackbar("Password Changed Successfully!")
                nav.navigateUp()
            }
        }

        return binding.root

    }

    private fun changePassword() {
        val currentPassword = binding.edtCurrentPassword.text.toString()
        newPassword = binding.edtPassword.text.toString()
        val confirmPassword = binding.edtPasswordConfirmation.text.toString()


        if (!isValid(newPassword, confirmPassword)) {
            return
        }

        viewModel.isCurrentPasswordValid(currentPassword)

    }


    private fun isValid(password: String, passwordConfirmation: String): Boolean {
        when {
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
}