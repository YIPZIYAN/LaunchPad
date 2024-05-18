package com.example.launchpad.interview

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.launchpad.R
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentScheduleInterviewBinding
import com.example.launchpad.job.viewmodel.JobViewModel
import com.example.launchpad.util.toBitmap
import com.example.launchpad.util.toast
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.SearchResultsView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = ScheduleInterviewFragment()
    }

    private val userVM: UserViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by viewModels()
    private val jobVM: JobViewModel by activityViewModels()
    private lateinit var binding: FragmentScheduleInterviewBinding
    private val nav by lazy { findNavController() }
    private val jobAppID by lazy { arguments?.getString("jobAppID") ?: "" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleInterviewBinding.inflate(inflater, container, false)
        binding.topAppBar.setOnClickListener { nav.navigateUp() }
        mapbox()
        fetchUserData()

        val constraint =
            CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraint)
                .build()

        val startTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Start Time")
                .build()

        val endTimePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(30)
                .setTitleText("End Time")
                .build()

        binding.chipDate.setOnClickListener {
            datePicker.show(childFragmentManager, "datePicker")
        }

        binding.chipStartTime.setOnClickListener {
            startTimePicker.show(childFragmentManager, "startTimePicker")
        }
        binding.chipEndTime.setOnClickListener {
            endTimePicker.show(childFragmentManager, "endTimePicker")
        }

        datePicker.addOnPositiveButtonClickListener {
            val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            binding.chipDate.text = format.format(Date(it))
        }

        startTimePicker.addOnPositiveButtonClickListener {
            binding.chipStartTime.text = "${startTimePicker.hour} : ${startTimePicker.minute}"
        }

        endTimePicker.addOnPositiveButtonClickListener {
            binding.chipEndTime.text = "${endTimePicker.hour} : ${endTimePicker.minute} $endTimePicker"
        }


        return binding.root
    }

    private fun fetchUserData() {
        jobAppVM.getJobAppLD().observe(viewLifecycleOwner) {
            var jobApp = jobAppVM.get(jobAppID)
            if (jobApp == null) {
                nav.navigateUp()
                toast("Applicant Data Is Empty!")
                return@observe
            }
            jobApp.user = userVM.get(jobApp.userId)!!
            jobApp.job = jobVM.get(jobApp.jobId)!!

            binding.applicantName.text = jobApp.user.name
            binding.avatarView.load(jobApp.user.avatar.toBitmap())
            binding.lblEmail.text = jobApp.user.email
            binding.lblJob.text = jobApp.job.jobName

        }
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