package com.example.launchpad.interview

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.viewmodel.ScheduleInterviewViewModel
import com.example.launchpad.databinding.FragmentScheduleInterviewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mapbox.search.autocomplete.PlaceAutocomplete

class ScheduleInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = ScheduleInterviewFragment()
    }

    private val userVM: UserViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by viewModels()
    private lateinit var binding: FragmentScheduleInterviewBinding
    private val nav by lazy { findNavController() }
    private val jobAppID by lazy { arguments?.getString("jobAppID") ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleInterviewBinding.inflate(inflater, container, false)

        val key = getString(R.string.mapbox_access_token)
        val placeAutocomplete = PlaceAutocomplete.create(key)
//        lifecycleScope.launchWhenCreated {
//            val response = placeAutocomplete.suggestions(
//                query = binding.edtLocation.text.toString(),
//            )
//
//            if (response.isValue) {
//                val suggestions = requireNotNull(response.value)
//
//
//                if (suggestions.isNotEmpty()) {
//                    // Supposing that a user has selected (clicked in UI) the first suggestion
//                    val selectedSuggestion = suggestions.first()
//                    val selectionResponse = placeAutocomplete.select(selectedSuggestion)
//                    selectionResponse.onValue { result ->
//                        Log.i("SearchApiExample", "Place Autocomplete result: $result")
//                    }.onError { e ->
//                        Log.i("SearchApiExample", "An error occurred during selection", e)
//                    }
//                }
//            } else {
//                Log.i("SearchApiExample", "Place Autocomplete error", response.error)
//            }
//        }


        val startDatePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Start Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
        val startTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Start Time")
                .build()
        val endTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(30)
                .setTitleText("Start Time")
                .build()
        binding.txtStart.setOnClickListener {
            startDatePicker.show(childFragmentManager, "startDatePicker")
        }
        binding.txtStartTime.setOnClickListener {
            startTimePicker.show(childFragmentManager, "startTimePicker")
        }
        binding.txtEndTime.setOnClickListener {
            endTimePicker.show(childFragmentManager, "endTimePicker")
        }

        return binding.root
    }


}