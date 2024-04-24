package com.example.launchpad.auth.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.UserActivity
import com.example.launchpad.auth.viewmodel.LoginViewModel
import com.example.launchpad.databinding.FragmentLoginBinding
import com.example.launchpad.util.toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

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
            Log.d("status", "onCreateView: logged in")
            intentToUserActivity()
        }

        if (viewModel.isLoggedIn()){
            //intent to
        }

        buttonAction()

        // check if login success
        viewModel.signInResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                intentToUserActivity()
            } else {
                toast(getString(R.string.exception_error_msg))
            }
        }

        return binding.root
    }

    private fun buttonAction() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_userActivity)
        }

        binding.txtSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.txtForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.btnGoogle.setOnClickListener { signInWithGoogle() }
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

    private fun intentToUserActivity() {
        val intent = Intent(requireActivity(), UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)

    }

}