package com.example.launchpad

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.launchpad.databinding.FragmentChatTextBinding
import com.example.launchpad.databinding.FragmentSettingBinding

class ChatTextFragment : Fragment() {

    companion object {
        fun newInstance() = ChatTextFragment()
    }

    private val viewModel: ChatTextViewModel by viewModels()
    private lateinit var binding: FragmentChatTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatTextBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}