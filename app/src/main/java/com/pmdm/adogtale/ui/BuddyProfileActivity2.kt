package com.pmdm.adogtale.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.pmdm.adogtale.R
import com.pmdm.adogtale.model.User
import com.pmdm.adogtale.model.Preferences
import com.pmdm.adogtale.model.Profile

class BuddyProfileActivity2 : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var btnPic1: ImageButton
    private lateinit var btnPic2: ImageButton
    private lateinit var btnPic3: ImageButton
    private lateinit var btnPic4: ImageButton
    private var selectedButton: ImageButton? = null

    private lateinit var user: User
    private lateinit var profile: Profile
    private lateinit var preferences: Preferences

    private val buttonUris = HashMap<ImageButton, String>()
    private val profilePics = HashMap<String, String>()

    val db = FirebaseFirestore.getInstance()

    private lateinit var acLookingFor: AutoCompleteTextView
    private lateinit var acPreferedBreed: AutoCompleteTextView
    private lateinit var acPreferedDistance: AutoCompleteTextView
    private lateinit var acPreferedLowestAge: AutoCompleteTextView
    private lateinit var acPreferedHighestAge: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile2)
        val btnDone: Button = findViewById(R.id.btnDone)
        var token_obtained: String?

        btnPic1 = findViewById(R.id.ibPic1)
        btnPic2 = findViewById(R.id.ibPic2)
        btnPic3 = findViewById(R.id.ibPic3)
        btnPic4 = findViewById(R.id.ibPic4)

        profile = intent.getSerializableExtra("profile") as Profile
        user = intent.getSerializableExtra("user") as User

        getFCMToken { token ->
            if (token != null) {
                // Haz algo con el token aquí
                Log.i("FCM token: ", token)
                user.token=token
            } else {
                Log.i("No se pudo obtener el token FCM", "NULO")
            }
        }

        preferences = Preferences("", "", "")


        // Looking For
        val lookingFor = resources.getStringArray(R.array.lookingFor)
        val arrayLookingFor = ArrayAdapter(this, R.layout.dropdown_menu, lookingFor)
        acLookingFor = findViewById(R.id.acLookingFor)
        acLookingFor.setAdapter(arrayLookingFor)

        // Prefered Breed
        val preferedBreed = resources.getStringArray(R.array.prefBreed)
        val arrayPrefBreed = ArrayAdapter(this, R.layout.dropdown_menu, preferedBreed)
        acPreferedBreed = findViewById(R.id.acPrefBreed)
        acPreferedBreed.setAdapter(arrayPrefBreed)

        // Prefered Distace
        val preferedDistance = resources.getStringArray(R.array.distance)
        val arrayPrefDistance = ArrayAdapter(this, R.layout.dropdown_menu, preferedDistance)
        acPreferedDistance = findViewById(R.id.acPreferedDistance)
        acPreferedDistance.setAdapter(arrayPrefDistance)

        // Prefered Lowest Age
        val preferedLowAge = resources.getStringArray(R.array.lowestAge)
        val arrayPrefLowAge = ArrayAdapter(this, R.layout.dropdown_menu, preferedLowAge)
        acPreferedLowestAge = findViewById(R.id.acPreferedLowAge)
        acPreferedLowestAge.setAdapter(arrayPrefLowAge)

        // Prefered Highest Age
        val preferedHighAge = resources.getStringArray(R.array.highestAge)
        val arrayPrefHighAge = ArrayAdapter(this, R.layout.dropdown_menu, preferedHighAge)
        acPreferedHighestAge = findViewById(R.id.acPreferedHighAge)
        acPreferedHighestAge.setAdapter(arrayPrefHighAge)

        btnDone.setOnClickListener() {
            createUserAccount(user)
        }

        btnPic1.setOnClickListener() {
            selectedButton = btnPic1
            openGallery()
        }
        btnPic2.setOnClickListener() {
            selectedButton = btnPic2
            openGallery()
        }
        btnPic3.setOnClickListener() {
            selectedButton = btnPic3
            openGallery()
        }
        btnPic4.setOnClickListener() {
            selectedButton = btnPic4
            openGallery()
        }
        firebaseAuth = Firebase.auth
    }

    // Create dog profile on Firebase
    private fun createProfileAccount(profile: Profile) {
        profile.town = user.town;
        profile.lookingFor = acLookingFor.text.toString()
        profile.prefBreed = acPreferedBreed.text.toString()
        profile.prefDistance = acPreferedDistance.text.toString()
        profile.preferedLowAge = acPreferedLowestAge.text.toString().toLong()
        profile.preferedHighAge = acPreferedHighestAge.text.toString().toLong()
        profile.pic1 = profilePics[btnPic1.id.toString()] ?: ""
        profile.pic2 = profilePics[btnPic2.id.toString()] ?: ""
        profile.pic3 = profilePics[btnPic3.id.toString()] ?: ""
        profile.pic4 = profilePics[btnPic4.id.toString()] ?: ""
        db.collection("profile").document(user.email).set(
            hashMapOf(
                "userEmail" to profile.userEmail,
                "name" to profile.name,
                "age" to profile.age,
                "gender" to profile.gender,
                "breed" to profile.breed,
                "something" to profile.something,
                "shortDescription" to profile.shortDescription,
                "lookingFor" to profile.lookingFor,
                "prefBreed" to profile.prefBreed,
                "prefDistance" to profile.prefDistance,
                "pic1" to profile.pic1,
                "pic2" to profile.pic2,
                "pic3" to profile.pic3,
                "pic4" to profile.pic4,
                "prefLowestAge" to profile.preferedLowAge,
                "prefHighestAge" to profile.preferedHighAge,
                "town" to profile.town
            )
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile created", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, CardSwipeActivity::class.java)
                startActivity(intent)
            }

        }

    }

    // Create user account on Firebase
    private fun createUserAccount(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Created account", Toast.LENGTH_SHORT).show()
                    db.collection("user").document(user.email).set(
                        hashMapOf(
                            "username" to user.username,
                            "name" to user.name,
                            "surname" to user.surname,
                            "town" to user.town,
                            "phone" to user.phone,
                            "token" to user.token,

                            )
                    ).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            createProfileAccount(profile)
                        }
                    }
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    // Open device gallery to choose pictures
    fun openGallery() {
        Log.i("OpenGallery", "Abriendo la galería para el botón ${selectedButton!!.id}")
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    // Set picture on image holder
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedButton?.setImageURI(data?.data)
            val imageUrl = data?.data
            imageButtonHavePicture(imageUrl!!)
        }
    }

    // Check for external storage permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Check if ImageButton already have an image
    private fun imageButtonHavePicture(uri: Uri) {
        val email = user.email
        val imageName = uri.lastPathSegment
        val storagePath = "$email/$imageName"
        val storageReference = FirebaseStorage.getInstance().getReference()
        val pictureReference = storageReference.child(storagePath)

        val existingImageUri = buttonUris[selectedButton]
        Log.i("existingImageUri: ", existingImageUri.toString())
        if (existingImageUri != null) {
            val oldPictureReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUri)
            Log.i("oldImageRegerence: ", oldPictureReference.toString())

            oldPictureReference.delete().addOnCompleteListener {
                uploadNewImage(pictureReference, uri)
            }.addOnFailureListener { e ->
                Log.e("Delete image error", e.message!!)
            }
        } else {
            uploadNewImage(pictureReference, uri)
        }
    }

    // Upload selected image
    private fun uploadNewImage(pictureReference: StorageReference, uri: Uri) {
        pictureReference.putFile(uri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri
                Toast.makeText(this, "Upload done!", Toast.LENGTH_SHORT).show()
                profilePics[selectedButton!!.id.toString()] = downloadUrl.toString()
                Log.i(
                    "UploadNewImage",
                    "Imagen subida para el botón ${selectedButton!!.id}, URL: $downloadUrl"
                )
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Uploading error", Toast.LENGTH_SHORT).show()
        }
    }

    fun getFCMToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Aquí puedes hacer cualquier cosa que necesites con el token, como almacenarlo en la base de datos
                // FirebaseUtil.currentUserDetails().update("fcmToken", token)
                callback(token)
            } else {
                callback(null)
            }
        }
    }

}
