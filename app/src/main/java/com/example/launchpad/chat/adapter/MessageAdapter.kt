package com.example.launchpad.chat.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.launchpad.data.ChatMessage
import com.example.launchpad.databinding.ItemMyMessageBinding
import com.example.launchpad.databinding.ItemOtherMessageBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(
    private val userID: String,
    val fn: (RecyclerView.ViewHolder, ChatMessage) -> Unit = { _, _ -> }
) : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(Diff) {

    companion object Diff : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(a: ChatMessage, b: ChatMessage) = a.id == b.id
        override fun areContentsTheSame(a: ChatMessage, b: ChatMessage) = a == b
    }

    class ViewHolderMyMessage(val binding: ItemMyMessageBinding) : RecyclerView.ViewHolder(binding.root)
    class ViewHolderOtherMessage(val binding: ItemOtherMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderID == userID) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> ViewHolderMyMessage(ItemMyMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> ViewHolderOtherMessage(ItemOtherMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        when (holder) {
            is ViewHolderMyMessage -> {
                holder.binding.txtMyMessage.text = message.message
                holder.binding.txtMyMessageTime.text = displaySendTime(message.sendTime)
            }
            is ViewHolderOtherMessage -> {
                holder.binding.txtOtherMessage.text = message.message
                holder.binding.txtOtherMessageTime.text = displaySendTime(message.sendTime)
            }
        }

        fn(holder, message)
    }

    private fun displaySendTime(sendTime: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(sendTime)
    }

}
