package com.pmdm.adogtale.utils

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.model.Profile
import com.pmdm.adogtale.model.User
import java.text.SimpleDateFormat
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.EmailAuthProvider
import com.pmdm.adogtale.AuthActivity

public class FirebaseUtil {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var fUser: FirebaseUser? = null
    fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    fun initDB(): FirebaseFirestore?  {
        db = FirebaseFirestore.getInstance()
        return db
    }

    fun initFirebaseAuth(): FirebaseAuth? {
        firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth
    }

    fun getCurrentFirebaseUser(): FirebaseUser? {
        initFirebase()
        initDB()!!
        initFirebaseAuth()!!
        fUser = firebaseAuth.currentUser
        Log.i("getCurrentFirebaseUser", fUser.toString())
        return fUser
    }

    fun currentUserDetails(): DocumentReference {
        return FirebaseFirestore.getInstance().collection("users")
            .document(fUser?.email.toString())
    }

    fun getCurrentUser(callback: (User) -> Unit) {
        initFirebase()
        fUser = firebaseAuth.currentUser
        var user: User
        Log.i("userEmail", fUser?.email.toString())
        db.collection("user").document(fUser?.email.toString()).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    user = User(
                        name = document.getString("name") ?: "",
                        phone = document.getString("phone") ?: "",
                        username = document.getString("username") ?: "",
                        surname = document.getString("surname") ?: "",
                        token = document.getString("token") ?: "",
                        town = document.getString("town") ?: "",
                        userId = fUser?.email.toString(),
                        email = fUser?.email.toString()
                    )
                    Log.i("getCurrentUser", user.name.toString())
                    callback(user)
                }
            }
    }

    fun getOtherUser(email: String, callback: (User) -> Unit) {
        initFirebase()
        initDB()!!
        initFirebaseAuth()!!
        var user: User
        Log.i("otherUserEmail", email)
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
                            town = document.getString("town") ?: "",
                            email = email,
                            userId = email
                        )
                        Log.i("otherUserName", user.name.toString())
                        callback(user)
                    }
                }
            }
    }

    fun allUserCollectionReference(userId: String? = null): DocumentReference {
        val user = userId ?: fUser?.email.toString()
        return FirebaseFirestore.getInstance().collection("users").document(user)
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

    fun getOtherUserFromChatroom(userIds: List<String?>): String {
        val currentUserEmail = fUser?.email.toString()
        return if (userIds[0] == currentUserEmail) {
            userIds[1]!!
        } else {
            userIds[0]!!
        }
    }

    fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("HH:MM").format(timestamp.toDate())
    }

    fun logout(context: Context) {
        Log.i("logout method", "aqui")
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, AuthActivity::class.java)
        context.startActivity(intent)
    }

    fun killActivity(context: Context) {
        Log.i("killActivity method", "aqui")
        val intent = Intent(context, AuthActivity::class.java)
        context.startActivity(intent)
    }

    suspend fun getOtherProfileData(email: String): Profile? = suspendCoroutine { continuation ->
        try {
            // Inicializar Firebase
            var otherProfile: Profile? = null
            initFirebase()
            initDB()!!
            initFirebaseAuth()!!
            Log.i("getOtherProfileData", "Iniciando búsqueda de perfil para el email: $email")
            db.collection("profile")
                .document(email)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("getOtherProfileData", "Consulta exitosa")
                        val document = task.result

                        if (document != null && document.exists()) {
                            Log.i("getOtherProfileData", "Perfil encontrado")
                            otherProfile = Profile(
                                name = document.getString("name") ?: "",
                                age = document.getString("age") ?: "",
                                gender = document.getString("gender") ?: "",
                                breed = document.getString("breed") ?: "",
                                shortDescription = document.getString("shortDescription") ?: "",
                                something = document.getString("something") ?: "",
                                userEmail = document.getString("userEmail") ?: "",
                                pic1 = document.getString("pic1") ?: "",
                                pic2 = document.getString("pic2") ?: "",
                                pic3 = document.getString("pic3") ?: "",
                                pic4 = document.getString("pic4") ?: "",
                                vid = document.getString("vid") ?: "",
                                lookingFor = document.getString("lookingFor") ?: "",
                                prefBreed = document.getString("prefBreed") ?: "",
                                prefDistance = document.getString("prefDistance") ?: "",
                                town = document.getString("town") ?: "",
                                preferedLowAge = document.getLong("prefLowestAge") ?: 99,
                                preferedHighAge = document.getLong("prefHighestAge") ?: 0
                            )
                            Log.i("getOtherProfileData", "Perfil: ${otherProfile!!.name}")
                            continuation.resume(otherProfile)
                        } else {
                            Log.i("getOtherProfileData", "No se encontró ningún perfil")
                            continuation.resume(null) // No se encontró ningún perfil
                        }
                    } else {
                        Log.i(
                            "getOtherProfileData",
                            "Error al obtener datos: ${task.exception?.message}"
                        )
                        continuation.resume(null) // Error al obtener datos
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("getOtherProfileData", "Error al obtener datos", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e("getOtherProfileData", "Excepción: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }


}
