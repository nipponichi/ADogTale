package com.pmdm.adogtale.ui

import android.net.Uri
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.pmdm.adogtale.R
import com.pmdm.adogtale.adapter.ChatRecyclerAdapter
import com.pmdm.adogtale.model.ChatMessageModel
import com.pmdm.adogtale.model.ChatroomModel
import com.pmdm.adogtale.model.User
import com.pmdm.adogtale.model.UserModel
import com.pmdm.adogtale.utils.AndroidUtil
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //get User
        targetEmail = intent.getStringExtra("targetEmail") as String

        //get User
        firebaseUtil.getOtherUser(targetEmail!!) { user ->
            otherUser = user
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
        otherUsername!!.setText(otherUser?.username)
        sendMessageBtn.setOnClickListener(View.OnClickListener { v: View? ->
            val message = messageInput!!.getText().toString().trim { it <= ' ' }
            if (message.isEmpty()) return@OnClickListener
            sendMessageToUser(message)
        })
        orCreateChatroomModel
        setupChatRecyclerView()
    }

    fun setupChatRecyclerView() {
        val query: Query = firebaseUtil.getChatroomMessageReference(chatroomId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<ChatMessageModel?> =
            FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel::class.java).build()
        adapter = ChatRecyclerAdapter(options, applicationContext)
        val manager = LinearLayoutManager(this)
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
        chatroomModel?.lastMessageTimestamp =Timestamp.now()
        val userIdsList = chatroomModel?.userIds?.toMutableList() ?: mutableListOf()
        userIdsList.add(fUser?.email.toString())
        chatroomModel?.userIds = userIdsList
        chatroomModel?.lastMessage = message
        firebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)

        val chatMessageModel =
            ChatMessageModel(message, fUser?.email.toString(), Timestamp.now())
        firebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
            .addOnCompleteListener(OnCompleteListener<DocumentReference?> { task ->
                if (task.isSuccessful) {
                    messageInput!!.setText("")
                    sendNotification(message)
                }
            })
    }

    //first time chat
    val orCreateChatroomModel: Unit
        get() {
            firebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    chatroomModel = task.getResult().toObject(ChatroomModel::class.java)
                    if (chatroomModel == null) {
                        //first time chat
                        chatroomModel = ChatroomModel(
                            chatroomId,
                            Arrays.asList(
                                fUser?.email.toString(),
                                targetEmail
                            ),
                            Timestamp.now(),
                            ""
                        )
                        firebaseUtil.getChatroomReference(chatroomId).set(chatroomModel!!)
                    }
                }
            }
        }

    fun sendNotification(message: String?) {
        var currentUser:User?=null
        firebaseUtil.currentUserDetails().get().addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                firebaseUtil.getCurrentUser { user ->
                    currentUser = user
                    Log.i("sendNotification username", currentUser?.name.toString())
                }
                try {
                    val jsonObject = JSONObject()
                    val notificationObj = JSONObject()
                    notificationObj.put("title", currentUser?.username)
                    notificationObj.put("body", message)
                    val dataObj = JSONObject()
                    dataObj.put("userId", fUser?.email.toString())
                    jsonObject.put("notification", notificationObj)
                    jsonObject.put("data", dataObj)
                    jsonObject.put("to", otherUser?.token)
                    Log.i("otherUser name", otherUser?.name.toString())
                    Log.i("json",jsonObject.toString())
                    callApi(jsonObject)
                } catch (e: Exception) {
                }
            }
        }
    }

    fun callApi(jsonObject: JSONObject) {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body: RequestBody = RequestBody.create(JSON, jsonObject.toString())
        val request: Request = Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer AIzaSyDXYckku5P4oE6QB51gr2JCGq6qmeRNlD4")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
            }
        })
    }
}