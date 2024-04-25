package com.pmdm.adogtale.controller

import android.util.Log
import com.pmdm.adogtale.model.Profile
import org.json.JSONObject
import java.io.IOException
import okhttp3.*
import org.json.JSONException

class ProfileFilter {
    // Breed filter
    fun filterByBreed(userProfile: Profile, otherProfile: Profile): Profile {
        if (userProfile.breed == otherProfile.breed){
            return otherProfile
        }
        return Profile()
    }

    // Gender filter
    fun filterByGender(userProfile: Profile, otherProfile: Profile): Profile {
        if(userProfile.gender != otherProfile.gender) {
            return otherProfile
        }
        return Profile()
    }

    // Asynchronic distance filter
    suspend fun filterByDistance(profile1: Profile, profile2: Profile): Profile? {
        val client = OkHttpClient()
        val url =
            "https://maps.googleapis.com/maps/api/distancematrix/json?origins=${profile1.town}&destinations=${profile2.town}&key=AIzaSyAz4lTqtyCWzyGF2SxjID2OAl-oHegMzIE"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val bodyString = response.body()?.string()
            try {
                val jsonObject = JSONObject(bodyString)
                val rows = jsonObject.getJSONArray("rows")
                val elements = rows.getJSONObject(0).getJSONArray("elements")
                if (elements.length() > 0) {
                    val distanceObject = elements.getJSONObject(0).optJSONObject("distance")
                    val distance = distanceObject?.optString("text")
                    if (!distance.isNullOrBlank()) {
                        Log.i("distance","La distancia entre ${profile1.town} y ${profile2.town} es $distance")
                        val prefDistance = profile1.prefDistance.replace("km", "").toDouble()
                        val distanceInKm = distance.replace("km", "").toDouble()

                        //Log.i("distance", "Máximo: " + prefDistance.toString() + ", Total:" + distanceInKm.toString())
                        // Check distance between profiles
                        if (prefDistance >= distanceInKm) {
                            Log.i("distance", "perfil bueno " + profile2.name)
                            return profile2
                        } else {
                            Log.i("distance", "La distancia es larga y grande para " + profile2.name)
                            return null
                        }
                    } else {
                        Log.i("distance", "No se encontró la distancia en el JSON")
                        return null
                    }
                } else {
                    Log.i("distance", "No se encontraron elementos en el JSON")
                    return null
                }
            } catch (e: JSONException) {
                Log.i("distance", "Error al parsear el JSON", e)
                return null
            }
        }
        return null
    }
}