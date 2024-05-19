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
import com.google.firebase.database.ChildEventListener
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


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
            Log.d("TOKEN", token)

        })

        chatList.clear()

        adapter = ChatAdapter { holder, chat ->
            holder.binding.chat.setOnClickListener { message(chat.id) }
        }
        binding.rvChat.adapter = adapter
        displayChatList(userVM.getAuth().uid)
        return binding.root
    }

    fun displayChatList(userID: String) {
        val chatRoomsRef = FirebaseDatabase.getInstance().getReference("chatRooms")

        chatRoomsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val chatRoomId = dataSnapshot.key
                if (chatRoomId != null && chatRoomId.contains(userID)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val userIDs = chatRoomId.split("_")
                        val receiver = withContext(Dispatchers.IO) {
                            if (userIDs[0] == userID) userVM.get(userIDs[1]) else userVM.get(userIDs[0])
                        }
                        val chat = Chat(
                            id = chatRoomId,
                            receiverName = receiver!!.name,
                            avatar = receiver.avatar,
                            latestMessage = getLatestMessage(chatRoomId)
                        )
                        latestMessageListener(chatRoomId)
                        withContext(Dispatchers.Main) {
                            chatList.add(chat)
                            adapter.submitList(chatList.sortedByDescending { it.latestMessage.sendTime })
                        }
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    private val messageListeners = mutableSetOf<String>()

    suspend fun latestMessageListener(chatRoomId: String) {
        if (messageListeners.contains(chatRoomId)) return
        messageListeners.add(chatRoomId)
        withContext(Dispatchers.IO) {
            val database = FirebaseDatabase.getInstance()
            val messageRef = database.getReference("chatRooms").child(chatRoomId).child("messages")
            var latestMessage = ChatMessage()

            val latestMessageQuery = messageRef.orderByChild("sendTime").limitToLast(1)

            latestMessageQuery.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(ChatMessage::class.java)
                    message?.let {
                        latestMessage = it
                        Log.d("LOOP", latestMessage.message)
                        // REFRESH THE CHAT LIST HERE
                        val updateChat = chatList.find { it.id == chatRoomId }
                        updateChat?.latestMessage = latestMessage
                        adapter.notifyDataSetChanged()
                        adapter.submitList(chatList.sortedByDescending { it.latestMessage.sendTime })
                    }
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onCancelled(p0: DatabaseError) {
                }

            })
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