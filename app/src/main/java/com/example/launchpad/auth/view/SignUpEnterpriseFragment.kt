package com.example.launchpad.auth.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentSignUpEnterpriseBinding

class SignUpEnterpriseFragment : Fragment() {

    lateinit var binding :FragmentSignUpEnterpriseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpEnterpriseBinding.inflate(inflater,container,false)


        return binding.root
    }

}