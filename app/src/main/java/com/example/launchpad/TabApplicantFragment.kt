package com.example.launchpad

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.launchpad.databinding.FragmentTabApplicantBinding

class TabApplicantFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): TabApplicantFragment {
            val fragment = TabApplicantFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: TabApplicantViewModel by viewModels()
    private lateinit var binding : FragmentTabApplicantBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_applicant, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(TabApplicantFragment.ARG_POSITION, 0) ?: 0
        when (position) {
            0 -> {
                //"IF NEW APPLICANT >= 1"
                binding.tabApplicant.visibility = View.VISIBLE
                binding.tabNoApplicant.visibility = View.GONE
                binding.tabNoAcceptApplicant.visibility = View.GONE
                binding.tabNoRejectApplicant.visibility = View.GONE
            }
            1 -> {
                //"IF ACCEPTED APPLICANT >= 1 ELSE DISPLAY NO APPLICANT"
                binding.tabApplicant.visibility = View.GONE
                binding.tabNoApplicant.visibility = View.GONE
                binding.tabNoAcceptApplicant.visibility = View.VISIBLE
                binding.tabNoRejectApplicant.visibility = View.GONE


            }
            2 -> {
                //"IF REJECTED APPLICANT >= 1 ELSE DISPLAY NO APPLICANT"
                binding.tabApplicant.visibility = View.GONE
                binding.tabNoApplicant.visibility = View.GONE
                binding.tabNoAcceptApplicant.visibility = View.GONE
                binding.tabNoRejectApplicant.visibility = View.VISIBLE
            }
        }
    }
}