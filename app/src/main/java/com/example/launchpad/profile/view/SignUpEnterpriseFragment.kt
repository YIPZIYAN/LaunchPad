package com.example.launchpad.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
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
        binding = DataBindingUtil.setContentView<FragmentSignUpEnterpriseBinding>(
            inflater,
            R.layout.fragment_sign_up_enterprise,
            container,
            false
        )
        binding.company = Company()
        binding.btnDone.setOnClickListener { submit() }
        binding.btnUpdate.setOnClickListener { update() }
        binding.topAppBar.setOnClickListener { nav.navigateUp() }

        companyVM.isSuccess.observe(viewLifecycleOwner) { nav.navigateUp() }
        userVM.getUserLD().observe(viewLifecycleOwner) {
            if (it.company_id != "" && it.isEnterprise) {
                updateUI(companyVM.get(it.company_id))
            }
        }


        return binding.root
    }

    private fun update() {
        lifecycleScope.launch {
            companyVM.update(binding.company)
        }
    }

    private fun updateUI(company: Company?) {
        if (company != null) {
            binding.company = company
        }
        binding.btnDone.visibility = View.GONE
        binding.btnUpdate.visibility = View.VISIBLE
    }

    private fun submit() {
        val company = getInput()

        lifecycleScope.launch {
            val companyId = companyVM.set(company)
            userVM.attachCompany(companyId)
        }

    }

    private fun getInput(): Company {
        return Company(
            name = binding.edtCompanyName.text.toString(),
            description = binding.edtCompanyDescription.text.toString(),
            location = binding.edtLocation.text.toString(),
            year = binding.edtYear.text.toString().toIntOrNull() ?: -1
        )
    }

}