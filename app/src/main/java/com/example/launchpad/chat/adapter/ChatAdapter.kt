package com.example.launchpad.chat.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpad.R
import com.example.launchpad.data.Chat
import com.example.launchpad.data.Job
import com.example.launchpad.databinding.ItemChatBinding
import com.example.launchpad.databinding.ItemJobCardBinding
import com.example.launchpad.job.adapter.JobAdapter

class ChatAdapter(
    val fn: (ViewHolder, Chat) -> Unit = { _, _ -> }
) : ListAdapter<Chat, ChatAdapter.ViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(a: Chat, b: Chat) = a.id == b.id
        override fun areContentsTheSame(a: Chat, b: Chat) = a == b
    }

    class ViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = getItem(position)

//      holder.binding.companyAvatar.setImageBlob(job.company.avatar)
        holder.binding.chatName.text = chat.receiverID
        fn(holder, chat)
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
