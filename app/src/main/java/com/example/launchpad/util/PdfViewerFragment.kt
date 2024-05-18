package com.example.launchpad.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.databinding.FragmentPdfViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class PdfViewerFragment : Fragment() {

    lateinit var binding: FragmentPdfViewerBinding
    val nav by lazy { findNavController() }
    private val fileName by lazy { arguments?.getString("fileName") ?: "" }
    private val url by lazy { arguments?.getString("url") ?: "" }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfViewerBinding.inflate(inflater, container, false)
        binding.topAppBar.title = "fileName"
        lifecycleScope.launch(Dispatchers.IO) {
            val inputStream = URL("https://firebasestorage.googleapis.com/v0/b/launchpad-1d2dd.appspot.com/o/resume%2F2024-05-18T06%3A02%3A37.164Z_Test_resume1.pdf%20?alt=media&token=2068f2a2-dc73-4b81-978d-45aa2b89a80f").openStream()
            withContext(Dispatchers.Main) {
                binding.PDFView.fromStream(inputStream).onRender { pages, _, _ ->
                    if (pages >= 1) {
                        binding.prograss.visibility = View.GONE
                    }
                }.load()
            }
        }
        return binding.root
    }


}