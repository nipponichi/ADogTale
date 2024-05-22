package com.pmdm.adogtale.ui.topbar.match_list

import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.pmdm.adogtale.R
import com.pmdm.adogtale.chat.ChatListActivity
import com.pmdm.adogtale.controller.ProfileActions
import com.pmdm.adogtale.matches.MatchesListActivity
import com.pmdm.adogtale.ui.CardSwipeActivity
import com.pmdm.adogtale.ui.UserProfileActivity
import com.pmdm.adogtale.ui.topbar.TopbarMenuOptionName
import com.squareup.picasso.Picasso

class MatchListTopbar(private val context: AppCompatActivity) {

    private val profileActions: ProfileActions = ProfileActions();

    fun configureTopbar() {
        // Encuentra la referencia de tu Toolbar
        val toolbar = context.findViewById<Toolbar>(R.id.topbar_matches)

        // Configura la Toolbar como la barra de acción de la actividad
        context.setSupportActionBar(toolbar)

        setTopbarListeners()

        // Opcional: si deseas ocultar el título predeterminado de la barra de acción
        context.supportActionBar?.setDisplayShowTitleEnabled(false)

        downloadProfileImageToTopbar()
    }

    private fun setTopbarListeners() {

        context.findViewById<ImageView>(R.id.matches_list_topbar_settings).setOnClickListener {

        }
        context.findViewById<ImageView>(R.id.matches_list_topbar_chats).setOnClickListener {
            val intent = Intent(this.context, ChatListActivity::class.java)
            this.context.startActivity(intent);
        }
        context.findViewById<ImageView>(R.id.matches_list_topbar_card_swipe).setOnClickListener {
            val intent = Intent(this.context, CardSwipeActivity::class.java)
            context.startActivity(intent)
        }
        context.findViewById<ImageView>(R.id.matches_list_topbar_profile).setOnClickListener {

        }
    }

    private fun onOptionsItemSelected(optionName: TopbarMenuOptionName): Boolean {

        Log.i("TopbarMenuActionListener", "dentro de onOptionsItemSelected: " + optionName.name)

        when (optionName) {

            TopbarMenuOptionName.MATCHES -> {
                val intent = Intent(this.context, MatchesListActivity::class.java)
                this.context.startActivity(intent);
                return true;
            }

            TopbarMenuOptionName.CHATS -> {
                val intent = Intent(this.context, ChatListActivity::class.java)
                this.context.startActivity(intent);
                return true;
            }

            TopbarMenuOptionName.PROFILE -> {
                val intent = Intent(this.context, UserProfileActivity::class.java)
                this.context.startActivity(intent);
                return true;
            }

            else -> {
                Toast.makeText(this.context, "Something went wrong in topbar", Toast.LENGTH_SHORT)
                    .show();
                return false;
            }
        }
    }

    private fun downloadProfileImageToTopbar() {
        val profileTopbarMenuOption =
            context.findViewById<ImageView>(R.id.matches_list_topbar_profile)

        profileActions.getCurrentProfile { profile ->

            Log.i("CardSwipeActivity", "toolbar pic1: " + profile.pic1)

            if (profile.pic1.isBlank()) {
                return@getCurrentProfile
            }

            Picasso.get()
                .load(profile.pic1)
                .into(profileTopbarMenuOption)
        }

    }


}