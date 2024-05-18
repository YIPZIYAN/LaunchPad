package com.example.launchpad.chat.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.chat.adapter.MessageAdapter
import com.example.launchpad.data.ChatMessage
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.viewmodel.ChatTextViewModel
import com.example.launchpad.databinding.FragmentChatTextBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.joda.time.DateTime

class ChatTextFragment : Fragment() {

    private val nav by lazy { findNavController() }
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter
    private var msgList = mutableListOf<ChatMessage>()

    private val chatRoomId by lazy { arguments?.getString("chatRoomId") ?: "" }


    companion object {
        fun newInstance() = ChatTextFragment()
    }

    private lateinit var binding: FragmentChatTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatTextBinding.inflate(inflater, container, false)

        binding.topAppBar.setOnClickListener {
            nav.navigateUp()
        }
        adapter = MessageAdapter(userVM.getAuth().uid)
        binding.rvMessages.adapter = adapter
        retriveMessages(chatRoomId)
        displayMessage(chatRoomId)

        binding.btnSend.setOnClickListener { sendMessage() }

        return binding.root
    }

    fun retriveMessages(chatRoomId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val messagesRef = FirebaseDatabase.getInstance().reference
                .child("chatRooms")
                .child(chatRoomId)
                .child("messages")

            val dataSnapshot = withContext(Dispatchers.Default) {
                messagesRef.get().await()
            }

            dataSnapshot.children.forEach {
                val message = ChatMessage(
                    id = it.child("id").getValue(String::class.java)!!,
                    message = it.child("message").getValue(String::class.java)!!,
                    sendTime = it.child("sendTime").getValue(Long::class.java)!!,
                    senderID = it.child("senderID").getValue(String::class.java)!!
                )
            }

            withContext(Dispatchers.Main) {
                adapter.submitList(msgList)
                binding.rvMessages.scrollToPosition(msgList.size -1)
            }

        }

    }

    fun sendMessage() {
        val messageText = binding.edtMessage.text.toString()
        if (messageText.isEmpty()) return

        val database = FirebaseDatabase.getInstance()
        val messageRef = database.getReference("chatRooms").child(chatRoomId).child("messages")
        val messageId = messageRef.push().key ?: return
        val message = ChatMessage(
            id = messageId,
            senderID = userVM.getAuth().uid,
            message = messageText,
            sendTime = DateTime.now().millis
        )

        messageRef.child(messageId).setValue(message).addOnSuccessListener {
            binding.edtMessage.text?.clear()
        }

    }


    fun displayMessage(chatRoomId: String) {
        val database = FirebaseDatabase.getInstance()
        val messageRef = database.getReference("chatRooms").child(chatRoomId).child("messages")

        messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    msgList.add(message)
                    binding.rvMessages.scrollToPosition(msgList.size -1)
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