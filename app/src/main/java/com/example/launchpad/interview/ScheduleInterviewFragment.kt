package com.example.launchpad.interview

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentScheduleInterviewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.SearchResultsView

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

        mapbox()


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
//        binding.txtStart.setOnClickListener
//        {
//            startDatePicker.show(childFragmentManager, "startDatePicker")
//        }
//        binding.txtStartTime.setOnClickListener
//        {
//            startTimePicker.show(childFragmentManager, "startTimePicker")
//        }
//        binding.txtEndTime.setOnClickListener
//        {
//            endTimePicker.show(childFragmentManager, "endTimePicker")
//        }

        return binding.root
    }

    private fun mapbox() {
        val key = getString(R.string.mapbox_access_token)
        val placeAutocomplete = PlaceAutocomplete.create(key)


        val placeAutocompleteUiAdapter = PlaceAutocompleteUiAdapter(
            view = binding.searchResult,
            placeAutocomplete = placeAutocomplete
        )

        binding.searchResult.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration()
            )
        )


        placeAutocompleteUiAdapter.addSearchListener(object :
            PlaceAutocompleteUiAdapter.SearchListener {

            override fun onSuggestionsShown(suggestions: List<PlaceAutocompleteSuggestion>) {
                // Nothing to do
            }

            override fun onSuggestionSelected(suggestion: PlaceAutocompleteSuggestion) {
                binding.edtLocation.setText(suggestion.name)
                binding.searchResult.visibility = View.INVISIBLE
            }

            override fun onPopulateQueryClick(suggestion: PlaceAutocompleteSuggestion) {
                binding.edtLocation.setText(suggestion.name)
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })


        binding.edtLocation.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                lifecycleScope.launchWhenStarted {
                    placeAutocompleteUiAdapter.search(text.toString())
                    if (text.isNotEmpty())
                        binding.searchResult.visibility = View.VISIBLE
                    else
                        binding.searchResult.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })
    }
}