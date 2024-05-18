package com.example.launchpad.chat.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.launchpad.chat.adapter.MessageAdapter
import com.example.launchpad.data.ChatMessage
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentChatTextBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.joda.time.DateTime
import org.json.JSONObject
import java.io.IOException


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
        adapter.submitList(msgList)
        displayMessage(chatRoomId)

        binding.btnSend.setOnClickListener { sendMessage() }

        return binding.root
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
            sendPushNotification(messageText)
        }

    }

    fun sendPushNotification(message:String) {
        val jsonObject = JSONObject()

        val notificationObj = JSONObject()
        notificationObj.put("title", "title")
        notificationObj.put("body", message)

        val dataObj = JSONObject()
        dataObj.put("userId", userVM.getAuth().uid)

        jsonObject.put("notification", notificationObj)
        jsonObject.put("data", dataObj)
        jsonObject.put("to", "eZn3vU0LSFCEjVaWNwxb3y:APA91bH__LKio3qwRFPhP-E9yn9SJHkJc4eWS12Eo1hW4nnBuyUoHdJyJ5obwHi5hz-CJuXii-5Y-WIWGlcO79-_j3rZ2M2fv8hHAGFCfaBKLd453uLmfyspEgHYGWw3Zbp1e0LMbXIY")

        callApi(jsonObject)
    }

    fun callApi(jsonObject: JSONObject) {
        val JSON: MediaType = "application/json".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body = RequestBody.create(JSON, jsonObject.toString())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer AAAAXcttPyk:APA91bGKCDLn2aO98ksp1j0vFskVqfdNKQAihmxM_UMY3Axib2R4czrUq1zYb4ZKsp1T60G_9Nj0Knwf5mHkg0ksrJQNDpPZK1ooME0CSX1RSN2CZisjlLru0hk3FYiTEsnAXSWsDlzt")
            .build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ERROR", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e("SUCCESS", response.toString())

            }

        })
    }

    fun displayMessage(chatRoomId: String) {
        val database = FirebaseDatabase.getInstance()
        val messageRef = database.getReference("chatRooms").child(chatRoomId).child("messages")

        messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    msgList.add(message)
                    binding.rvMessages.scrollToPosition(msgList.size - 1)
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