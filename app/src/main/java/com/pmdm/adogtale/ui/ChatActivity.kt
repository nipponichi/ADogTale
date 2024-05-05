package com.pmdm.adogtale.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.pmdm.adogtale.R
import com.pmdm.adogtale.adapter.ChatRecyclerAdapter
import com.pmdm.adogtale.model.ChatMessageModel
import com.pmdm.adogtale.model.ChatroomModel
import com.pmdm.adogtale.model.User
import com.pmdm.adogtale.utils.FirebaseUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Arrays

class ChatActivity : AppCompatActivity() {
    val firebaseUtil = FirebaseUtil()
    val currentUser = firebaseUtil.getCurrentFirebaseUser()
    var chatroomId: String? = null
    var chatroomModel: ChatroomModel? = null
    var adapter: ChatRecyclerAdapter? = null
    var messageInput: EditText? = null
    var backBtn: ImageButton? = null
    var otherUsername: TextView? = null
    var recyclerView: RecyclerView? = null
    var imageView: ImageView? = null
    val fUser = firebaseUtil.getCurrentFirebaseUser()
    var targetEmail:String? = null
    var otherUser: User? = null
    val jsonObject = JSONObject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //get User
        targetEmail = intent.getStringExtra("targetEmail") as String

        //get User
        firebaseUtil.getOtherUser(targetEmail!!) { user ->
            otherUser = user
            Log.i("otherUserNameChat", otherUser!!.name.toString())
            otherUsername!!.setText(otherUser?.username)
            Log.i("username",otherUser?.username.toString())
        }
        chatroomId = firebaseUtil.getChatroomId(fUser?.email.toString(), targetEmail!!)
        messageInput = findViewById(R.id.chat_message_input)
        val sendMessageBtn:ImageButton = findViewById(R.id.message_send_btn) as ImageButton
        backBtn = findViewById(R.id.back_btn)
        otherUsername = findViewById(R.id.other_username)
        recyclerView = findViewById(R.id.chat_recycler_view)
        imageView = findViewById(R.id.profile_pic_image_view)

        val backBtn: ImageButton = findViewById(R.id.back_btn) as ImageButton
        backBtn.setOnClickListener { v: View? -> onBackPressed() }

        sendMessageBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val message = messageInput!!.getText().toString().trim { it <= ' ' }
            if (message.isEmpty()) return@OnClickListener
            sendMessageToUser(message)
        })

        getOrCreateChatroomModel()
        setupChatRecyclerView()
        Log.i("onCreate msg","final")
    }

    fun setupChatRecyclerView() {
        Log.i("setupChat", "dentro")
        val query: Query = firebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        Log.i("setupChat query", query.toString())
        val options: FirestoreRecyclerOptions<ChatMessageModel?> =
            FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel::class.java).build()
        Log.i("setupChat options", options.toString())
        adapter = ChatRecyclerAdapter(options, applicationContext)
        Log.i("setupChat adapter", adapter.toString())
        val manager = LinearLayoutManager(this)
        Log.i("setupChat manager", manager.toString())
        manager.reverseLayout = true
        recyclerView!!.layoutManager = manager
        recyclerView!!.adapter = adapter
        adapter!!.startListening()
        adapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView!!.smoothScrollToPosition(0)
            }
        })
    }

    fun sendMessageToUser(message: String?) {
        //Log.i("msg message",message.toString())
        chatroomModel?.lastMessageTimestamp = Timestamp.now()
        //Log.i("msg chatroom", chatroomModel?.chatroomId.toString())
        val userIdsList = chatroomModel?.userIds?.toMutableList() ?: mutableListOf()
        //Log.i("msg userIdsList", userIdsList.toString())
        //userIdsList.add(fUser?.email.toString())
        chatroomModel?.userIds = userIdsList
        //Log.i("msg chatroom IDs", chatroomModel?.userIds.toString())
        chatroomModel?.lastMessage = message
        Log.i("msg chatroom ID", chatroomId.toString())
        Log.i("msg chatroom Model", chatroomModel.toString())
        firebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)

        val chatMessageModel =
            ChatMessageModel(message, fUser?.email.toString(), Timestamp.now())
        Log.i("msg chatMessageModel", chatMessageModel.senderId.toString())
        Log.i("msg chatMessageModel", chatMessageModel.message.toString())
        Log.i("msg chatMessageModel", chatMessageModel.timestamp.toString())
        firebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("task", "dentro")
                    messageInput!!.setText("")
                    sendNotification(message)
                }
            }
    }

    fun getOrCreateChatroomModel() {
        Log.i("create chatroom", "dentro")
        firebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
            Log.i("create chatroom task", task.toString())
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel::class.java)
                Log.i("create chatroom 1", chatroomModel.toString())
                if (chatroomModel == null) {
                    //first time chat
                    chatroomModel = ChatroomModel(
                        chatroomId,
                        Arrays.asList(fUser?.email.toString(), otherUser?.userId),
                        Timestamp.now(),
                        ""
                    )
                    firebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
                    Log.i("create chatroom 2", chatroomModel.toString())
                }
            }
        }
    }


    fun sendNotification(message: String?) {

        var currentUser:User?=null
        firebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            if (task.isSuccessful()) {

                try {

                    val notificationObj = JSONObject()
                    firebaseUtil.getCurrentUser { user ->
                        currentUser = user
                        Log.i("sendNotification username", currentUser?.name.toString())
                        notificationObj.put("title", currentUser?.username)
                        notificationObj.put("body", message)
                        val dataObj = JSONObject()
                        dataObj.put("userId", fUser?.email.toString())
                        jsonObject.put("notification", notificationObj)
                        jsonObject.put("data", dataObj)
                        jsonObject.put("to", otherUser?.token)
                        Log.i("otherUser name", otherUser?.name.toString())
                        Log.i("json", jsonObject.toString())
                        Log.i("json title", notificationObj.get("title") as String)
                        callApi(jsonObject)
                    }

                } catch (e: Exception) {
                }
            }
        }
    }

    fun callApi(jsonObject: JSONObject) {
        Log.i("callApi jsonObject", jsonObject.toString())
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        Log.i("callApi JSON", JSON.toString())
        val client = OkHttpClient()
        Log.i("callApi client", client.toString())
        val url = "https://fcm.googleapis.com/fcm/send"
        val body: RequestBody = RequestBody.create(JSON, jsonObject.toString())
        Log.i("callApi body", body.toString())
        val request: Request = Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer YOUR_API_KEY")
            .build()
        Log.i("callApi request", request.toString())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.i("callApi onFailure", "dentro")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                Log.i("callApi onResponse", call.toString())
                Log.i("callApi onResponse", response.toString())
            }
        })
    }
}