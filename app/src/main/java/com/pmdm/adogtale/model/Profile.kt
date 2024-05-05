package com.pmdm.adogtale.model

import java.io.Serializable

data class Profile(
    var name: String = "",
    var age: String = "",
    var gender: String = "",
    var breed: String = "",
    var shortDescription: String = "",
    var something: String = "",
    var userEmail: String = "",
    var pic1: String = "",
    var pic2: String = "",
    var pic3: String = "",
    var pic4: String = "",
    var vid: String = "",
    var lookingFor: String = "",
    var prefBreed: String = "",
    var prefDistance: String = "",
    var preferedHighAge: Long = 99,
    var preferedLowAge: Long = 0,
    var town: String = "",
) : Serializable
