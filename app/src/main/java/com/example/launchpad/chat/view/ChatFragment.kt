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
import com.example.launchpad.data.ChatMessage
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentChatBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.childEvents
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TOKEN", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

        })

        chatList.clear()

        binding.btnPostJob.setOnClickListener { createChatroom("BIQY4hjJBbOtA61eBWVpC3ykB5f2_64guPgKSSlPgbaxMkEAwZrK4cqG2") }

        adapter = ChatAdapter { holder, chat ->
            holder.binding.chat.setOnClickListener { message(chat.id) }
        }
        binding.rvChat.adapter = adapter
        displayChatList(userVM.getAuth().uid)
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

            val deferredList = mutableListOf<Deferred<Boolean>>()

            dataSnapshot.children.forEach {
                val chatRoomId = it.key
                if (chatRoomId != null && chatRoomId.contains(userID)) {
                    val deferred = async(Dispatchers.IO) {
                        val userIDs = chatRoomId.split("_")
                        val chat = Chat(
                            id = chatRoomId,
                            receiverID = if (userIDs[0] == userID) userIDs[1] else userIDs[0],
                            latestMessage = getLatestMessage(chatRoomId)
                        )
                        withContext(Dispatchers.Main) {
                            chatList.add(chat)
                        }
                    }
                    deferredList.add(deferred)

                }
            }

            deferredList.awaitAll()

            withContext(Dispatchers.Main) {
                adapter.submitList(chatList)
            }
        }

    }

    suspend fun getLatestMessage(chatRoomId: String): ChatMessage {
        return withContext(Dispatchers.IO) {
            var latestMessage = ChatMessage()

                val messageRef =
                    FirebaseDatabase.getInstance().getReference("chatRooms/$chatRoomId/messages")
                val dataSnapshot = withContext(Dispatchers.Default) {
                    messageRef.orderByChild("sendTime").limitToLast(1).get().await()
                }

                if (dataSnapshot.exists()) {
                    Log.d("SNAPSHOT", dataSnapshot.toString())
                }

                dataSnapshot.children.forEach {
                    latestMessage = ChatMessage(
                        id = it.child("id").getValue(String::class.java)!!,
                        message = it.child("message").getValue(String::class.java)!!,
                        sendTime = it.child("sendTime").getValue(Long::class.java)!!,
                        senderID = it.child("senderID").getValue(String::class.java)!!
                    )
                }

            latestMessage
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