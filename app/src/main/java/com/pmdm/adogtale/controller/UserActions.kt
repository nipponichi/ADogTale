package com.pmdm.adogtale.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.model.User

class UserActions {

    private lateinit var firebaseAuth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()
    private lateinit var localUser: User

    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun getCurrentUser(callback: (User) -> Unit) {
        initFirebase()
        val user = firebaseAuth.currentUser
        db.collection("user").document(user?.email.toString()).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    localUser = User(
                        email = document.getString("email") ?: "",
                        username = document.getString("username") ?: "",
                        phone = document.getString("phone") ?: "",
                        name = document.getString("name") ?: "",
                        surname = document.getString("surname") ?: "",
                        town = document.getString("town") ?: "",
                    )
                    callback(localUser)
                }
            }
    }


}
