package com.pmdm.adogtale.utils

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pmdm.adogtale.model.User
import java.text.SimpleDateFormat

public class FirebaseUtil {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var fUser:FirebaseUser? = null
    fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        initFirebase()
        fUser = firebaseAuth.currentUser
        return fUser
    }

    val isLoggedIn: Boolean
        get() = if (getCurrentFirebaseUser() != null) {
            true
        } else false

    fun currentUserDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("users")
            .document(fUser?.email.toString())
    }

    fun getCurrentUser(callback: (User) -> Unit) {
        initFirebase()
        var user: User
        db.collection("user").document(fUser?.email.toString()).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    user = User(
                        name = document.getString("name") ?: "",
                        phone = document.getString("phone") ?: "",
                        username = document.getString("username") ?: "",
                        token = document.getString("token") ?: "",
                        town = document.getString("town") ?: "",
                    )
                    callback(user)
                }
            }
    }

    fun getOtherUser(email: String, callback: (User) -> Unit) {
        initFirebase()
        var user: User
        db.collection("user").document(email).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        user = User(
                            name = document.getString("name") ?: "",
                            phone = document.getString("phone") ?: "",
                            username = document.getString("username") ?: "",
                            token = document.getString("token") ?: "",
                            town = document.getString("town") ?: ""
                        )
                        callback(user)
                    }
                }
            }
    }


        fun allUserCollectionReference(): CollectionReference {
            return FirebaseFirestore.getInstance().collection("users")
        }

        fun getChatroomReference(chatroomId: String?): DocumentReference {
            return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId!!)
        }

        fun getChatroomMessageReference(chatroomId: String?): CollectionReference {
            return getChatroomReference(chatroomId).collection("chats")
        }

        fun getChatroomId(userId1: String, userId2: String): String {
            return if (userId1.hashCode() < userId2.hashCode()) {
                userId1 + "_" + userId2
            } else {
                userId2 + "_" + userId1
            }
        }

        fun allChatroomCollectionReference(): CollectionReference {
            return FirebaseFirestore.getInstance().collection("chatrooms")
        }

        fun getOtherUserFromChatroom(userIds: List<String>): DocumentReference {
            return if (userIds[0] == fUser?.email.toString()) {
                allUserCollectionReference().document(userIds[1])
            } else {
                allUserCollectionReference().document(userIds[0])
            }
        }

        fun timestampToString(timestamp: Timestamp): String {
            return SimpleDateFormat("HH:MM").format(timestamp.toDate())
        }

        fun logout() {
            FirebaseAuth.getInstance().signOut()
        }

    }
