package com.example.launchpad.interview.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpad.data.Interview
import com.example.launchpad.databinding.ItemInterviewBinding
import com.example.launchpad.databinding.ItemIntervieweeBinding
import com.example.launchpad.util.displayDate
import com.example.launchpad.util.formatTime
import com.example.launchpad.util.toBitmap
import io.getstream.avatarview.coil.loadImage

class InterviewAdapter(
    val fn: (ViewHolder, Interview) -> Unit = { _, _ -> }
) : ListAdapter<Interview, InterviewAdapter.ViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<Interview>() {
        override fun areItemsTheSame(a: Interview, b: Interview) = a.id == b.id
        override fun areContentsTheSame(a: Interview, b: Interview) = a == b
    }

    class ViewHolder(val binding: ItemInterviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemInterviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val interview = getItem(position)
        Log.d("TAG", "onBindViewHolder: ")
        val prev = if (position > 0) getItem(position - 1) else null

        holder.binding.avatarView.loadImage(interview.jobApp.user.avatar.toBitmap())
        holder.binding.applicantName.text = interview.jobApp.user.name
        holder.binding.appliedJob.text = interview.jobApp.job.jobName

        holder.binding.location.text = interview.location
        holder.binding.video.text = interview.video

        if (interview.location == "") {
            holder.binding.location.visibility = View.GONE
        }

        if (interview.video == "") {
            holder.binding.video.visibility = View.GONE
        }

        if (interview.remark == "") {
            holder.binding.remark.visibility = View.GONE
        }

        holder.binding.remark.text = interview.remark

        if (prev != null && prev.date == interview.date) {
            holder.binding.lblDate.visibility = View.GONE
        } else {
            holder.binding.lblDate.text = "${displayDate(interview.date)}"
        }

        holder.binding.startTime.text =
            formatTime(interview.startTime.hour, interview.startTime.minutes)
        holder.binding.endTime.text = formatTime(interview.endTime.hour, interview.endTime.minutes)

        fn(holder, interview)
    }


}
