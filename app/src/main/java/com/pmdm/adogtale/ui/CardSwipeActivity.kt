package com.pmdm.adogtale.ui

import android.content.Intent
import com.pmdm.adogtale.R
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_swipe)
        initDatabase()
        initImports()
        getCurrentProfile()

    }

    // Paginate profile results
    private fun paginate() {
        val old = adapter.items
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("paginación", "paginación");
                val baru = task.result
                if (baru.isEmpty()) {
                    Toast.makeText(this, "No hay más tarjetas para mostrar", Toast.LENGTH_LONG)
                        .show()
                    adapter.items = old
                    adapter.notifyDataSetChanged()
                } else {
                    val callback = CardStackCallback(old, baru)
                    val hasil = DiffUtil.calculateDiff(callback)
                    adapter.items = baru
                    hasil.dispatchUpdatesTo(adapter)
                }
            } else {
                Toast.makeText(
                    this,
                    "No se pudo cargar la información de la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Add compatible profile into a list
    private fun addList(): Task<List<Itemx>> {
        val taskSource = TaskCompletionSource<List<Itemx>>()
        val items = mutableListOf<Itemx>()
        otherProfileActions.getOtherProfile(userDogProfile!!) { profile ->
            otherDogProfile = profile
            items.add(
                Itemx(
                    otherDogProfile!!.pic1,
                    otherDogProfile!!.name,
                    otherDogProfile!!.age,
                    otherDogProfile!!.shortDescription
                )
            )
            if (!taskSource.task.isComplete) {
                taskSource.setResult(items)
            }
        }
        return taskSource.task
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

                    Direction.Left -> Toast.makeText(
                        this@CardSwipeActivity,
                        "Direction Left",
                        Toast.LENGTH_SHORT
                    ).show()

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

                // Recarga la cola de cards cuando solamente quedan 5
                if (manager.topPosition == adapter.itemCount - 5) {
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
                Log.d(TAG, "onCardAppeared: " + position + ", nama: " + tv?.text)
            }

            override fun onCardDisappeared(view: View?, position: Int) {
                val tv = view?.findViewById<TextView>(R.id.item_name)
                Log.d(TAG, "onCardDisappeared: " + position + ", nama: " + tv?.text)
            }
        })

        // Loads card with items from list
        addList().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("addCards", "adding cards");
                val items = task.result
                adapter = CardStackAdapter(items)
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
                    //MATCH!
                    Toast.makeText(this, "IT'S A MATCH!", Toast.LENGTH_SHORT).show()
                    Log.d("ORTU2", "${documentos.data}")
                    val intent = Intent(this, SplashScreenActivity::class.java)
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



