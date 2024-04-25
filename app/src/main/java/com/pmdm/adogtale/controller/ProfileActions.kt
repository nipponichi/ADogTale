package com.pmdm.adogtale.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.model.Profile

class ProfileActions {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var profile: Profile

    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    fun getCurrentProfile(callback: (Profile) -> Unit) {
        initFirebase()
        val user = firebaseAuth.currentUser
        db.collection("profile").document(user?.email.toString()).get()
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    profile = Profile(
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
                    callback(profile)
                }
            }
    }
}