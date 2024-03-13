package com.example.launchpad.view

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentLoginBinding
import com.example.launchpad.databinding.FragmentSignUpBinding
import com.example.launchpad.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    private val viewModel: SignUpViewModel by viewModels()
    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentSignUpBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSignUpBinding.inflate(inflater,container,false)



        binding.txtSignIn.setOnClickListener {
            nav.navigate(R.id.loginFragment)

        }

        return binding.root
    }
}