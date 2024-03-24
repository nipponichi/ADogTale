package com.pmdm.adogtale.model

import java.io.Serializable

data class Profile (
    var user: User,
    var name: String,
    var age: String,
    var pic1: String? = null,
    var pic2: String? = null,
    var pic3: String? = null,
    var pic4: String? = null,
    var vid: String? = null,
) : Serializable
