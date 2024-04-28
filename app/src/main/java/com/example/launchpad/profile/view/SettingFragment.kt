package com.example.launchpad.profile.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.MainActivity
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentSettingBinding
import com.example.launchpad.util.intentWithoutBackstack
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private val userVM: UserViewModel by activityViewModels()
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
            requireActivity().deleteSharedPreferences("company")
            requireContext().intentWithoutBackstack(requireContext(), MainActivity::class.java)
        }
    }

}