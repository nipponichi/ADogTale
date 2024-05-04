package com.pmdm.adogtale.ui

import android.content.Intent
import android.net.Uri
import com.pmdm.adogtale.R
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.*
import com.pmdm.adogtale.controller.CardStackAdapter
import com.pmdm.adogtale.controller.CardStackCallback
import com.pmdm.adogtale.controller.OtherProfileActions
import com.pmdm.adogtale.controller.ProfileActions
import com.pmdm.adogtale.model.Itemx
import com.pmdm.adogtale.model.ProfilesMatching
import com.pmdm.adogtale.model.Profile

class CardSwipeActivity : AppCompatActivity() {
    private val TAG = "CardSwipeActivity"

    private lateinit var adapter: CardStackAdapter
    private lateinit var profileMatching: ProfilesMatching
    private val TAG2 = "ORTU"
    private lateinit var profileActions: ProfileActions
    private lateinit var otherProfileActions: OtherProfileActions


    private lateinit var firebaseAuth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    private var userDogProfile: Profile? = null
    private var otherDogProfile: Profile? = null
    var targetEmail:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_swipe)
        initDatabase()
        initImports()
        getCurrentProfile()

    }

    // Paginate profile results
    /*private fun paginate() {
        val old = adapter.items
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("paginación", "paginación")
                val new = task.result
                if (new.isEmpty()) {
                    // Si no hay más perfiles disponibles, muestra el texto
                    Toast.makeText(this, "No more profiles available 1", Toast.LENGTH_LONG).show()
                } else {
                    val callback = CardStackCallback(old, new)
                    val res = DiffUtil.calculateDiff(callback)
                    adapter.items = new
                    res.dispatchUpdatesTo(adapter)
                }
            } else {
                Toast.makeText(
                    this,
                    "No se pudo cargar la información de la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.addOnCompleteListener {
            if (!it.isSuccessful || it.result.isEmpty()) {
                // Si la tarea no tuvo éxito o la lista de perfiles es vacía,
                // muestra el texto "no more profiles available"
                Toast.makeText(this, "No more profiles available 2", Toast.LENGTH_LONG).show()
            }
        }
    }*/
    private fun paginate() {
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val new = task.result
                Log.d("paginate", "Data fetched: $new")
                if (new.isNullOrEmpty()) {
                    Toast.makeText(this, "No more profiles available", Toast.LENGTH_LONG).show()
                } else {

                    val callback = CardStackCallback(adapter.items, new)
                    val diffResult = DiffUtil.calculateDiff(callback)
                    adapter.items = new
                    Log.d("paginate", "Adapter items: ${adapter.items}")
                    Toast.makeText(this, "Success: " + adapter.itemCount, Toast.LENGTH_LONG).show()
                    diffResult.dispatchUpdatesTo(adapter)
                }
            } else {
                Toast.makeText(this, "Failed to load database information", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Add compatible profile into a list
    private fun addList(): Task<List<Itemx>> {
        val taskSource = TaskCompletionSource<List<Itemx>>()

        // Llamar a getOtherProfiles y obtener la lista de perfiles
        otherProfileActions.getOtherProfiles(userDogProfile!!) { profiles ->
            val items = mutableListOf<Itemx>()

            // Iterar sobre cada perfil y agregar un elemento Itemx a la lista de items
            profiles.forEach { profile ->
                items.add(
                    Itemx(
                        profile.pic1,
                        profile.name,
                        profile.age,
                        profile.shortDescription
                    )
                )
            }
            // Añade una card final
            items.add(
                Itemx(
                    getResourceUri(R.drawable.end_card),
                    "",
                    "",
                    ""
                )
            )

            // Establecer el resultado en la tarea
            if (!taskSource.task.isComplete) {
                taskSource.setResult(items)
            }
        }

        return taskSource.task
    }
    // Last card path
    private fun getResourceUri(@DrawableRes drawableId: Int): String {
        return Uri.parse("android.resource://" + packageName + "/" + drawableId).toString()
    }

    // Get logged in user's dog profile
    private fun getCurrentProfile() {
        profileActions.getCurrentProfile { profile ->
            userDogProfile = profile
            Toast.makeText(this, userDogProfile?.name, Toast.LENGTH_SHORT).show()
            setupCardStackView()

        }
    }

    fun setupCardStackView() {
        val cardStackView = findViewById<CardStackView>(R.id.card_stack_view)

        var manager = CardStackLayoutManager(this, null)

        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setSwipeableMethod(SwipeableMethod.Manual)
        manager.setOverlayInterpolator(LinearInterpolator())

        manager = CardStackLayoutManager(this, object : CardStackListener {

            override fun onCardDragging(direction: Direction?, ratio: Float) {
                Log.d(TAG, "onCardDragging: d=" + direction?.name + " ratio=" + ratio)
            }

            override fun onCardSwiped(direction: Direction?) {
                when (direction) {
                    Direction.Right -> {
                        Toast.makeText(
                            this@CardSwipeActivity,
                            "Direction Right",
                            Toast.LENGTH_SHORT
                        ).show()
                        profileMatching = ProfilesMatching(
                            "ortu20@hotmail.com",
                            "Tobi20",
                            "ortu30@hotmail.com",
                            "Tobi30"
                        )
                        saveLike(profileMatching)
                        checkingANewMatchExist()
                    }

                    Direction.Top -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Top",
                        Toast.LENGTH_SHORT
                    ).show()
//                    Direction.Left -> Toast.makeText(
//                        this@CardSwipeActivity,
//                        "Direction Left",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    Direction.Right -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Right",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        Toast.makeText(this@CardSwipeActivity, "Take care", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                // Recarga la cola de cards
                if (manager.topPosition == adapter.itemCount) {
                    Toast.makeText(this@CardSwipeActivity, "Nothing more to show", Toast.LENGTH_SHORT).show()
                    paginate()
                }

            }

            override fun onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.topPosition)
            }

            override fun onCardCanceled() {
                Log.d(TAG, "onCardCanceled: " + manager.topPosition)
            }

            override fun onCardAppeared(view: View?, position: Int) {
                val tv = view?.findViewById<TextView>(R.id.item_name)
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv?.text)
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                val tv = view?.findViewById<TextView>(R.id.item_name)
                Log.d(TAG, "onCardDisappeared: " + position + ", name: " + tv?.text)
            }
        })

        // Loads card with items from list
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("addCards", "adding cards");
                val items = task.result
                adapter = CardStackAdapter(items, manager)
                cardStackView.layoutManager = manager
                cardStackView.adapter = adapter
                cardStackView.itemAnimator = DefaultItemAnimator()
            } else {
                Toast.makeText(this, "Could not load database information", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveLike(profilesMatching: ProfilesMatching) {
        Log.d(TAG2, "AQUI")
        // Método para guardar el perfil que ha sido gustado
        val data = hashMapOf(
            "user_original" to profilesMatching.user_original,
            "profile_original" to profilesMatching.profile_original,
            "user_target" to profilesMatching.user_target,
            "profile_target" to profilesMatching.profile_target,
            "likeAlreadyChecked" to profilesMatching.likeAlreadyChecked,
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("profiles_matching")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Like guardado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar el like: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun checkingANewMatchExist() {
        val db = FirebaseFirestore.getInstance()
        db.collection("profiles_matching").whereEqualTo("user_target", "ortu30@hotmail.com")
            .whereEqualTo("profile_target", "Tobi30").whereEqualTo("likeAlreadyChecked", false)
            .get().addOnSuccessListener {
                it
                for (documentos in it) {
                    targetEmail = documentos.getString("user_target")
                    //MATCH!
                    Toast.makeText(this, "IT'S A MATCH!", Toast.LENGTH_SHORT).show()
                    Log.d("ORTU2", "${documentos.data}")
                    val intent = Intent(this, SplashScreenActivity::class.java)
                    intent.putExtra("targetEmail", targetEmail)
                    startActivity(intent)
                }
            }

    }

    // Initialize database instance
    private fun initDatabase() {
        firebaseAuth = FirebaseAuth.getInstance()

    }


    // Initialize other class imports
    private fun initImports() {
        profileActions = ProfileActions()
        otherProfileActions = OtherProfileActions()
    }

}


/*
     private fun addList(): List<Itemx> {
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", name, age, user))
        items.add(Itemx("https://pamipe.com/wiki/wp-content/uploads/2022/09/Bichon-Maltes-2.jpg", name, age, user))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", name, age, user))
        items.add(Itemx("https://www.kiwoko.com/blogmundoanimal/wp-content/uploads/2023/06/bichon-maltes-informacion.jpg", name, age, user))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", name, age, user))

        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", "Markonah", "24", "Jember"))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", "Marpuah", "20", "Malang"))
        items.add(Itemx("https://www.kiwoko.com/blogmundoanimal/wp-content/uploads/2023/06/bichon-maltes-informacion.jpg", "Sukijah", "27", "Jonggol"))
        items.add(Itemx("https://upload.wikimedia.org/wikipedia/commons/thumb/e/ee/1AKC_Maltese_Dog_Show_2011.jpg/250px-1AKC_Maltese_Dog_Show_2011.jpg", "Markobar", "19", "Bandung"))
        items.add(Itemx("https://pamipe.com/wiki/wp-content/uploads/2022/09/Bichon-Maltes-2.jpg", "Marmut", "25", "Hutan"))

        return items
    }*/



