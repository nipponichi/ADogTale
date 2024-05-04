package com.pmdm.adogtale.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pmdm.adogtale.model.UserModel

object AndroidUtil {
    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun passUserModelAsIntent(intent: Intent, model: UserModel) {
//        intent.putExtra("username", model.getUsername())
//        intent.putExtra("phone", model.getPhone())
//        intent.putExtra("userId", model.getUserId())
//        intent.putExtra("fcmToken", model.getFcmToken())
    }

    fun getUserModelFromIntent(intent: Intent): UserModel {
        val userModel = UserModel()
//        userModel.setUsername(intent.getStringExtra("username"))
//        userModel.setPhone(intent.getStringExtra("phone"))
//        userModel.setUserId(intent.getStringExtra("userId"))
//        userModel.setFcmToken(intent.getStringExtra("fcmToken"))
        return userModel
    }

    fun setProfilePic(context: Context?, imageUri: Uri?, imageView: ImageView?) {
        Glide.with(context!!).load(imageUri).apply(RequestOptions.circleCropTransform()).into(
            imageView!!
        )
    }
}
