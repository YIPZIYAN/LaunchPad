package com.example.launchpad.chat.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.launchpad.R
import com.example.launchpad.chat.adapter.MessageAdapter
import com.example.launchpad.data.ChatMessage
import com.example.launchpad.data.User
import com.example.launchpad.data.viewmodel.UserViewModel
import com.example.launchpad.databinding.FragmentChatTextBinding
import com.example.launchpad.util.toBitmap
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class ChatTextFragment : Fragment() {

    private val nav by lazy { findNavController() }
    private val userVM: UserViewModel by activityViewModels()
    private lateinit var adapter: MessageAdapter
    private var msgList = mutableListOf<ChatMessage>()

    private val chatRoomId by lazy { arguments?.getString("chatRoomId") ?: "" }
    private lateinit var currentUser: User
    private lateinit var otherUser: User

    private val SERVER_KEY =
        "AAAAXcttPyk:APA91bGKCDLn2aO98ksp1j0vFskVqfdNKQAihmxM_UMY3Axib2R4czrUq1zYb4ZKsp1T60G_9Nj0Knwf5mHkg0ksrJQNDpPZK1ooME0CSX1RSN2CZisjlLru0hk3FYiTEsnAXSWsDlzt"

    companion object {
        fun newInstance() = ChatTextFragment()
    }

    private lateinit var binding: FragmentChatTextBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatTextBinding.inflate(inflater, container, false)

        val userIDs = chatRoomId.split("_")

        //TO SOLVE DISPLAY NAME ISSUE
        currentUser = userVM.get(userVM.getAuth().uid)!!

        otherUser =
            if (userIDs[0] == userVM.getAuth().uid) {
                userVM.get(userIDs[1])!!
            } else {
                userVM.get(userIDs[0])!!
            }

        binding.topAppBar.title = otherUser.name
        val avatar =
            if (otherUser.avatar.toBytes().isEmpty())
                R.drawable.round_account_circle_24
            else
                otherUser.avatar.toBitmap()

        adapter = MessageAdapter(currentUser.uid, avatar)
        binding.rvMessages.adapter = adapter
        adapter.submitList(msgList)
        displayMessage(chatRoomId)

        binding.btnSend.setOnClickListener { sendMessage() }

        binding.topAppBar.setOnClickListener {
            nav.navigateUp()
        }

        onlineStatusListener(otherUser.uid)
        updateLastSeenTime(chatRoomId, currentUser.uid)

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
            senderID = currentUser.uid,
            message = messageText,
            sendTime = DateTime.now().millis
        )

        messageRef.child(messageId).setValue(message).addOnSuccessListener {
            binding.edtMessage.text?.clear()
            sendPushNotification(messageText)
        }

    }

    fun sendPushNotification(message: String) {
        val jsonObject = JSONObject()

        val notificationObj = JSONObject()
        notificationObj.put("title", currentUser.name)
        notificationObj.put("body", message)

        val dataObj = JSONObject()
        dataObj.put("chatRoomId", chatRoomId)

        jsonObject.put("notification", notificationObj)
        jsonObject.put("data", dataObj)
        jsonObject.put("to", otherUser.token)

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
            .header(
                "Authorization",
                "Bearer $SERVER_KEY"
            )
            .build()
        client.newCall(request).enqueue(object : Callback {
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

    fun updateLastSeenTime(chatRoomId: String, userId: String) {
        val lastSeenRef = FirebaseDatabase.getInstance().getReference("chatRooms")
            .child(chatRoomId).child("lastSeen").child(userId)

        Log.d("LASTSEENUSERID", userId)

        lastSeenRef.setValue(ServerValue.TIMESTAMP)
    }

    suspend fun getLastSeenTime(chatRoomId: String, userId: String): Long {

        return suspendCoroutine { cont ->
            val lastSeenRef = FirebaseDatabase.getInstance().getReference("chatRooms")
                .child(chatRoomId).child("lastSeen").child(userId)
            val listener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lastSeenTime = dataSnapshot.getValue(Long::class.java)
                    Log.d("LASTSEENTIME", lastSeenTime.toString())
                    cont.resume(lastSeenTime ?: 0L)
                    lastSeenRef.removeEventListener(this) // Remove the listener


                }

                override fun onCancelled(p0: DatabaseError) {
                    cont.resume(0L)
                }
            }
            lastSeenRef.addValueEventListener(listener)
        }

    }

    fun onlineStatusListener(userId: String) {
        val onlineStatusRef =
            FirebaseDatabase.getInstance().getReference("onlineStatus").child(userId)
        onlineStatusRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val isOnline = dataSnapshot.getValue(Boolean::class.java)
                isOnline?.let {
                    if (isOnline) {
                        binding.topAppBar.subtitle = "Online"
                    } else {
                        lifecycleScope.launch {
                            binding.topAppBar.subtitle =
                                displayLastSeenTime(getLastSeenTime(chatRoomId, otherUser.uid))
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    fun displayLastSeenTime(lastSeenTime: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        return "Last Seen: " + sdf.format(lastSeenTime)
    }

    override fun onPause() {
        super.onPause()
        updateLastSeenTime(chatRoomId, currentUser.uid)
    }

    override fun onResume() {
        super.onResume()
        updateLastSeenTime(chatRoomId, currentUser.uid)
    }

}