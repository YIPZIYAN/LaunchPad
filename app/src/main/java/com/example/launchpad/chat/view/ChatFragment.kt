package com.example.launchpad.chat.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.viewmodel.ChatViewModel
import com.example.launchpad.R
import com.example.launchpad.chat.adapter.ChatAdapter
import com.example.launchpad.data.Chat
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private val nav by lazy { findNavController() }
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatAdapter
    private val userVM: UserViewModel by activityViewModels()
    private var chatList = mutableListOf<Chat>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)

        chatList.clear()

        binding.btnPostJob.setOnClickListener{ createChatroom("BIQY4hjJBbOtA61eBWVpC3ykB5f2_64guPgKSSlPgbaxMkEAwZrK4cqG2") }

        adapter = ChatAdapter { holder, chat ->
            holder.binding.chat.setOnClickListener { message(chat.id) }
        }
        binding.rvChat.adapter = adapter
        displayChatList(userVM.getAuth().uid)
        Log.d("DISPLAY", chatList.toString())
        return binding.root
    }

    fun createChatroom(chatRoomId: String) {
        val chatRoomsRef = FirebaseDatabase.getInstance().getReference("chatRooms")
        chatRoomsRef.child(chatRoomId).get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                chatRoomsRef.child(chatRoomId).setValue(true)
            }
        }
    }

    fun displayChatList(userID: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val chatRoomsRef = FirebaseDatabase.getInstance().getReference("chatRooms")
            val dataSnapshot = withContext(Dispatchers.Default) {
                chatRoomsRef.get().await()
            }
            dataSnapshot.children.forEach {
                val chatRoomId = it.key
                if (chatRoomId != null && chatRoomId.contains(userID)) {
                    val userIDs = chatRoomId.split("_")
                    val chat = Chat(
                        id = chatRoomId,
                        receiverID = if (userIDs[0] == userID) userIDs[1] else userIDs[0]
                    )
                    chatList.add(chat)
                }
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(chatList)
                Log.d("DISPLAY2", chatList.toString())
            }
        }

    }

    private fun message(chatRoomId: String) {
        nav.navigate(
            R.id.chatTextFragment, bundleOf(
                "chatRoomId" to chatRoomId
            )
        )
    }


}