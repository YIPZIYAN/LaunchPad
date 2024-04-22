package com.example.launchpad.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpad.R
import com.example.launchpad.data.Job
import com.example.launchpad.databinding.ItemJobCardBinding
import com.example.launchpad.auth.view.LoginFragment

class JobAdapter (
    val fn: (ViewHolder, Job) -> Unit = { _, _ -> }
): ListAdapter<Job, JobAdapter.ViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(a: Job, b: Job) = a.jobName == b.jobName
        override fun areContentsTheSame(a: Job, b: Job) = a == b
    }

    class ViewHolder(val binding: ItemJobCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemJobCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val job = getItem(position)
        // company
        if (LoginFragment.userType == 0) {
            holder.binding.bookmark.visibility = View.GONE
        }
        holder.binding.jobCard.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_jobDetailsFragment)
        }
        holder.binding.jobName.text = job.jobName
        holder.binding.lblSalary.text = "RM ${job.minSalary} - RM ${job.maxSalary} per month"
        holder.binding.chipJobType.text = job.jobType
        holder.binding.chipWorkplace.text = job.workplace
        holder.binding.chipPosition.text = job.position
        holder.binding.timePosted.text = displayPostTime(job.postTime!!)

    }

    private fun displayPostTime(postTime: Long): String {
        return DateUtils.getRelativeTimeSpanString(
            postTime,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        ).toString()
    }

}
