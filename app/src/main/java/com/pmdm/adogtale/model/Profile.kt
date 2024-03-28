package com.pmdm.adogtale.model

import java.io.Serializable

data class Profile(
    var name: String,
    var age: String,
    var gender: String,
    var breed: String,
    var shortDescription: String,
    var something: String,
    var userEmail: String? = null,
    var pic1: String? = null,
    var pic2: String? = null,
    var pic3: String? = null,
    var pic4: String? = null,
    var vid: String? = null,
) : Serializable
