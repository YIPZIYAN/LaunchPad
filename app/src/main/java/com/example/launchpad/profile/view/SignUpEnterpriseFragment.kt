package com.example.launchpad.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.launchpad.EmailVerificationActivity
import com.example.launchpad.auth.viewmodel.SignUpViewModel
import com.example.launchpad.data.Company
import com.example.launchpad.databinding.FragmentSignUpEnterpriseBinding
import com.example.launchpad.util.intentWithoutBackstack
import com.example.launchpad.util.toast

class SignUpEnterpriseFragment : Fragment() {

    lateinit var binding: FragmentSignUpEnterpriseBinding
    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpEnterpriseBinding.inflate(inflater, container, false)
        binding.btnSignUp.setOnClickListener { submit() }

        signUpViewModel.errorResponseMsg.observe(viewLifecycleOwner) { toast(it) }

        signUpViewModel.isSignUpSuccess.observe(viewLifecycleOwner) {
            requireContext().intentWithoutBackstack(
                requireActivity(),
                EmailVerificationActivity::class.java
            )
        }

        return binding.root
    }

    private fun submit() {
        val company = Company(
            name = binding.edtCompanyName.text.toString(),
            description = binding.edtCompanyDescription.text.toString(),
            location = binding.edtLocation.text.toString(),
            year = binding.edtYear.text.toString().toIntOrNull() ?: -1
        )

//        signUpViewModel.signUpWithEmail(email, password)

    }

}