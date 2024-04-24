package com.example.launchpad.auth.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.EmailVerificationActivity
import com.example.launchpad.R
import com.example.launchpad.UserActivity
import com.example.launchpad.auth.viewmodel.LoginViewModel
import com.example.launchpad.databinding.FragmentLoginBinding
import com.example.launchpad.util.displayErrorHelper
import com.example.launchpad.util.intentWithoutBackstack
import com.example.launchpad.util.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlin.system.exitProcess

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
        var userType = 0; // 1 = employee, 0 = company, testing only
    }

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        //auto login
        if (viewModel.isLoggedIn() && viewModel.isVerified()) {
            Log.d("status", "onCreateView: logged in and verified")
            requireContext().intentWithoutBackstack(requireActivity(), UserActivity::class.java)
        }

        if (viewModel.isLoggedIn()) {
            Log.d("status", "onCreateView: logged in")
            requireContext().intentWithoutBackstack(
                requireActivity(),
                EmailVerificationActivity::class.java
            )
        }

        buttonAction()

        // check if login success
        viewModel.signInResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                requireContext().intentWithoutBackstack(requireActivity(), UserActivity::class.java)
            } else {
//                toast(getString(R.string.exception_error_msg))
            }
        }

        viewModel.response.observe(viewLifecycleOwner) { toast(it) }

        return binding.root
    }

    private fun buttonAction() {
        binding.btnLogin.setOnClickListener { signInWithEmail() }

        binding.txtSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.txtForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
    }

    private fun signInWithEmail() {
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (!isValid(email, password)) {
            return
        }

        viewModel.firebaseAuthWithEmail(email, password)
    }

    private fun isValid(email: String, password: String): Boolean {
        when {
            email == "" || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                displayErrorHelper(binding.lblEmail, "Invalid email address.")
                return false
            }

            password == "" -> {
                displayErrorHelper(binding.lblPassword, "Wrong password")
                return false
            }

            else -> return true
        }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, Activity.RESULT_OK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account.idToken!!)
                Log.d("success", "onActivityResult: getID")
            } catch (e: ApiException) {
                Log.d("error", "onActivityResult: error")
                toast(getString(R.string.exception_error_msg))
            }
        }
    }


}