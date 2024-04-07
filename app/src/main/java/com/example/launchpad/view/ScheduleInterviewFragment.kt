package com.example.launchpad.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.launchpad.R
import com.example.launchpad.viewmodel.ScheduleInterviewViewModel
import com.example.launchpad.databinding.FragmentScheduleInterviewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class ScheduleInterviewFragment : Fragment() {

    companion object {
        fun newInstance() = ScheduleInterviewFragment()
    }

    private val viewModel: ScheduleInterviewViewModel by viewModels()
    private lateinit var binding: FragmentScheduleInterviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_schedule_interview, container, false)
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