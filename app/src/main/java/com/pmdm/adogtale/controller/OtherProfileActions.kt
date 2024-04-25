package com.pmdm.adogtale.controller

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.model.Itemx
import com.pmdm.adogtale.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtherProfileActions {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var otherProfile: Profile
    private lateinit var profileFilter: ProfileFilter
    val profilesWithCards = mutableSetOf<Profile>()

    // Initialize database instances
    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    // Obtain profiles from database
    fun getOtherProfile(profile: Profile, callback: (Profile?) -> Unit) {
        profileFilter = ProfileFilter()
        initFirebase()
        db.collection("profile")
            .whereEqualTo("lookingFor", profile.lookingFor)
            .whereEqualTo("prefBreed", profile.prefBreed)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        if (profile.userEmail != document.getString("userEmail")) {
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

                            Log.i("otherProfile1", otherProfile.name)

                            // Filters coroutine for breed, gender and distance
                            GlobalScope.launch(Dispatchers.IO) {
                                val filteredProfileByDistance = profileFilter.filterByDistance(profile, otherProfile)
                                //Log.i("otherProfile2", filteredProfileByDistance.name)
                                if (filteredProfileByDistance != null) {
                                    // Looking for a specific reference
                                    if (profile.lookingFor.equals("Pair") || profile.prefBreed.equals("Same as mine")) {
                                        Log.i("filter", "perfil " + filteredProfileByDistance.lookingFor + " " + filteredProfileByDistance.name)
                                        val filteredProfileByBreed = profileFilter.filterByBreed(profile, filteredProfileByDistance)
                                        if (filteredProfileByBreed != null) {
                                            Log.i("otherProfile3", filteredProfileByBreed.name)
                                            // Just profiles that passed all the filters
                                            withContext(Dispatchers.Main) {
                                                if (!profilesWithCards.contains(otherProfile)) {
                                                   profilesWithCards.add(otherProfile)
                                                    callback(otherProfile)
                                                }
                                            }
                                        }
                                    } else {
                                        // Don't requires additional filters
                                        withContext(Dispatchers.Main) {
                                            Log.i("no filter", "pefil " + otherProfile.lookingFor + " " + otherProfile.name)
                                            if (!profilesWithCards.contains(otherProfile)) {
                                                profilesWithCards.add(otherProfile)
                                                callback(otherProfile)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

}