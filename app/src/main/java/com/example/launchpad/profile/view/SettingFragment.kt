package com.example.launchpad.profile.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentSettingBinding
import com.example.launchpad.profile.viewmodel.SettingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val viewModel: SettingViewModel by viewModels()
    private lateinit var binding: FragmentSettingBinding
    private val auth = Firebase.auth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.cardLogout.setOnClickListener {
            signOut()
            true
        }

        return binding.root
    }

    private fun signOut() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth.signOut()

        googleSignInClient.signOut().addOnSuccessListener {
            // Optional: Update UI or show a message to the user
            Log.d("UI", "signOut: navigate to login")
            findNavController().navigate(R.id.action_settingFragment_to_mainActivity)

        }
    }

}