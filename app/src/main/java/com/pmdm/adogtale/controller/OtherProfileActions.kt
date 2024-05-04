package com.pmdm.adogtale.controller

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pmdm.adogtale.model.Itemx
import com.pmdm.adogtale.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class OtherProfileActions {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var otherProfile: Profile
    private lateinit var profileFilter: ProfileFilter
    private var filteredByBreed: Profile? = null
    private var filteredByGender: Profile? = null
    private var filteredByAge: Profile? = null
    val profilesWithCards = mutableSetOf<Profile>()


    // Initialize database instances
    fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    // Obtain users compatible profiles
    fun getOtherProfiles(profile: Profile, callback: (List<Profile>) -> Unit) {
        // Crear un filtro de perfil (ProfileFilter)
        val profileFilter = ProfileFilter()
        // Inicializar Firebase
        initFirebase()

        // Acceder a la colección "profile" en la base de datos
        db.collection("profile")
            // Filtrar documentos donde el campo "lookingFor" es igual al valor de "lookingFor" en el perfil dado
            .whereEqualTo("lookingFor", profile.lookingFor)
            // Filtrar documentos donde el campo "prefBreed" es igual al valor de "prefBreed" en el perfil dado
            .whereEqualTo("prefBreed", profile.prefBreed)
            // Obtener los resultados de la consulta
            .get()
            // Agregar un listener para completar la tarea
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profilesPassedFilter = mutableListOf<Profile>()

                    // Usar una lista de Deferred para almacenar las corrutinas
                    val deferredList = mutableListOf<Deferred<Profile?>>()

                    // Iterar sobre los documentos obtenidos
                    for (document in task.result) {
                        // Inicializar otherProfile con un valor predeterminado
                        var otherProfile = Profile()

                        if (!profile.name.toString().equals(document.getString("name"))) {
                            // Actualizar otherProfile solo si la condición se cumple
                            otherProfile = Profile(
                                // Asignar valores de los campos del documento al objeto Profile, con valores predeterminados si el campo es nulo
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
                        }

                        // Iniciar una corrutina para llamar a los métodos de ProfileFilter de forma asíncrona y almacenar el resultado en la lista deferredList
                        val deferred = GlobalScope.async(Dispatchers.IO) {
//                            // Llamar a filterByDistance y almacenar el resultado
//                            val filteredByDistance =
//                                profileFilter.filterByDistance(profile, otherProfile)
//
//                            if (filteredByDistance != null) {
//                                var filteredProfile =
//                                    otherProfile // Perfil filtrado para ser agregado si pasa todos los filtros
//
//                                // Filtrar por género si se busca pareja
//                                if (otherProfile.lookingFor.equals("Pair")) {
//                                    filteredProfile =
//                                        profileFilter.filterByGender(profile, filteredProfile)
//                                }
//
//                                // Filtrar por raza si se busca la misma
//                                if (otherProfile.prefBreed.equals("Same as mine")) {
//                                    filteredProfile =
//                                        profileFilter.filterByBreed(profile, filteredProfile)
//                                }
//
//                                // Filtrar por edad
//                                filteredProfile =
//                                    profileFilter.filterByAge(profile, filteredProfile)
//
//                                // Si el perfil pasa todos los filtros, agregarlo a la lista
//                                if (filteredProfile != null) {
//                                    filteredProfile
//                                } else {
//                                    null
//                                }
//                            } else {
//                                null
//                            }
                            if (otherProfile != null) {
                                otherProfile
                            } else {
                                null
                            }
                        }

                        deferredList.add(deferred)


                        // Esperar que todas las corrutinas se completen y agregar los perfiles que pasan los filtros a profilesPassedFilter
                        GlobalScope.launch(Dispatchers.Main) {
                            deferredList.awaitAll().forEach { filteredProfileByAllFilters ->
                                if (filteredProfileByAllFilters != null) {
                                    profilesPassedFilter.add(filteredProfileByAllFilters)
                                }
                            }

                            // Llamar al callback con la lista de perfiles que pasan los filtros
                            callback(profilesPassedFilter)
                        }
                    }
                }
            }
    }
}

