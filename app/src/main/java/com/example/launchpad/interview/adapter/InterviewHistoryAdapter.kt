package com.example.launchpad.interview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpad.data.Interview
import com.example.launchpad.data.JobApplication
import com.example.launchpad.databinding.ItemApplicantBinding
import com.example.launchpad.databinding.ItemIntervieweeBinding
import com.example.launchpad.job_application.adapter.ApplicantAdapter
import com.example.launchpad.util.displayDate
import com.example.launchpad.util.displayPostTime
import com.example.launchpad.util.toBitmap
import io.getstream.avatarview.coil.loadImage

class InterviewHistoryAdapter(
    val fn: (ViewHolder, Interview) -> Unit = { _, _ -> }
) : ListAdapter<Interview, InterviewHistoryAdapter.ViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<Interview>() {
        override fun areItemsTheSame(a: Interview, b: Interview) = a.id == b.id
        override fun areContentsTheSame(a: Interview, b: Interview) = a == b
    }

    class ViewHolder(val binding: ItemIntervieweeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemIntervieweeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val interview = getItem(position)

        holder.binding.avatarView.loadImage(interview.jobApp.user.avatar.toBitmap())
        holder.binding.applicantName.text = interview.jobApp.user.name
        holder.binding.appliedJob.text = interview.jobApp.job.jobName
        holder.binding.lblDay.text = "Interview on ${displayDate(interview.date)}"
        fn(holder, interview)
    }


}
