package com.example.launchpad.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.data.Company
import com.example.launchpad.data.viewmodel.CompanyViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentSignUpEnterpriseBinding
import kotlinx.coroutines.launch

class SignUpEnterpriseFragment : Fragment() {

    lateinit var binding: FragmentSignUpEnterpriseBinding
    private val companyVM: CompanyViewModel by activityViewModels()
    private val userVM: UserViewModel by activityViewModels()
    private val nav by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpEnterpriseBinding.inflate(inflater, container, false)
        binding.btnDone.setOnClickListener { submit() }

        binding.topAppBar.setOnClickListener { nav.navigateUp() }

        return binding.root
    }

    private fun submit() {
        val company = Company(
            name = binding.edtCompanyName.text.toString(),
            description = binding.edtCompanyDescription.text.toString(),
            location = binding.edtLocation.text.toString(),
            year = binding.edtYear.text.toString().toIntOrNull() ?: -1
        )

        lifecycleScope.launch { companyVM.set(company) }


    }

}