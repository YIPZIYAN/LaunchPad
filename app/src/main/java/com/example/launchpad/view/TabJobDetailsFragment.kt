package com.example.launchpad.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentTabJobDetailsBinding
import com.example.launchpad.viewmodel.TabJobDetailsViewModel


class TabJobDetailsFragment : Fragment() {

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): TabJobDetailsFragment {
            val fragment = TabJobDetailsFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: TabJobDetailsViewModel by viewModels()
    private lateinit var binding: FragmentTabJobDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_tab_job_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val position = arguments?.getInt(ARG_POSITION, 0) ?: 0
        when (position) {
            0 -> {
                binding.tabDesc.visibility = View.VISIBLE
                binding.tabCompany.visibility = View.GONE
            }
            1 -> {
                binding.tabCompany.visibility = View.VISIBLE
                binding.tabDesc.visibility = View.GONE
            }
        }
    }



}