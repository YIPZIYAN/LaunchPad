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
import coil.load
import com.example.launchpad.R
import com.example.launchpad.data.Interview
import com.example.launchpad.data.Time
import com.example.launchpad.data.viewmodel.InterviewViewModel
import com.example.launchpad.data.viewmodel.JobApplicationViewModel
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentScheduleInterviewBinding
import com.example.launchpad.data.viewmodel.JobViewModel
import com.example.launchpad.util.dialog
import com.example.launchpad.util.disable
import com.example.launchpad.util.displayDate
import com.example.launchpad.util.snackbar
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
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScheduleInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = ScheduleInterviewFragment()
    }

    private val userVM: UserViewModel by activityViewModels()
    private val jobAppVM: JobApplicationViewModel by activityViewModels()
    private val jobVM: JobViewModel by activityViewModels()
    private val interviewVM: InterviewViewModel by viewModels()
    private lateinit var binding: FragmentScheduleInterviewBinding
    private val nav by lazy { findNavController() }
    private val jobAppID by lazy { arguments?.getString("jobAppID") ?: "" }
    private val interviewID by lazy { arguments?.getString("interviewID") ?: "" }
    private val action by lazy { arguments?.getString("action") ?: "" }

    private var date: Long = 0
    private var startTime: Time = Time()
    private var endTime: Time = Time()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleInterviewBinding.inflate(inflater, container, false)
        binding.topAppBar.setOnClickListener { nav.navigateUp() }

        fetchUserData()


        interviewVM.isSuccess.observe(viewLifecycleOwner) {
            if (it) {
                nav.popBackStack(R.id.homeFragment, false)
                snackbar("Interview Scheduled Successfully!")
            }
        }

        binding.btnApply.setOnClickListener { submit() }

        when (action) {
            "VIEW" -> {
                viewModeUI()
                fetchInterviewDate()
            }

            "EDIT" -> {
                binding.topAppBar.title = "Edit Interview"
                binding.btnApply.text = "EDIT"
                fetchInterviewDate()
                mapbox()
                setupDateTimePicker()
            }

            else -> {
                mapbox()
                setupDateTimePicker()
            }
        }

        return binding.root
    }

    private fun viewModeUI() {
        binding.apply {
            btnApply.disable()
            topAppBar.title = "Interview History"
            btnApply.text = "VIEW ONLY"
            chipEndTime.disable()
            chipStartTime.disable()
            chipDate.disable()
            edtLocation.disable()
            edtRemark.disable()
            edtVideo.disable()
        }
    }

    private fun fetchInterviewDate() {
        interviewVM.getInterviewLD().observe(viewLifecycleOwner) {
            val interview = interviewVM.get(interviewID)
            if (interview == null) {
                nav.navigateUp()
                toast("Interview Data Is Empty!")
                return@observe
            }

            binding.edtLocation.setText(interview.location)
            binding.edtRemark.setText(interview.remark)
            binding.edtVideo.setText(interview.video)

            binding.chipDate.text = displayDate(interview.date)
            binding.chipStartTime.text =
                format("%02d : %02d", interview.startTime.hour, interview.startTime.minutes)
            binding.chipEndTime.text =
                format("%02d : %02d", interview.endTime.hour, interview.endTime.minutes)

            date = interview.date
            startTime = interview.startTime
            endTime = interview.endTime
        }
    }

    private fun submit() {
        val location = binding.edtLocation.text.toString().trim()
        val video = binding.edtVideo.text.toString().trim()
        val remark = binding.edtRemark.text.toString().trim()

        if (location == "" && video == "") {
            toast("Please fill in location or video conferencing link.")
            return
        }

        if (date == 0L || startTime == Time() || endTime == Time()) {
            toast("Please select date and time.")
            return
        }

        val interview = Interview(
            id = interviewID,
            jobAppID = jobAppID,
            location = location,
            video = video,
            remark = remark,
            startTime = startTime,
            endTime = endTime,
            date = date
        )

        val title = if (action == "EDIT") "Edit Interview" else "Schedule Interview"
        dialog(title, "Are you sure to ${title.lowercase()}?",
            onPositiveClick = { _, _ ->
                lifecycleScope.launch {
                    interviewVM.set(interview)
                }
            })
    }

    private fun setupDateTimePicker() {
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
            date = it
        }

        startTimePicker.addOnPositiveButtonClickListener {
            binding.chipStartTime.text =
                format("%02d : %02d", startTimePicker.hour, startTimePicker.minute)
            startTime = Time(startTimePicker.hour, startTimePicker.minute)
        }

        endTimePicker.addOnPositiveButtonClickListener {
            binding.chipEndTime.text =
                format("%02d : %02d", endTimePicker.hour, endTimePicker.minute)
            endTime = Time(endTimePicker.hour, endTimePicker.minute)

        }
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