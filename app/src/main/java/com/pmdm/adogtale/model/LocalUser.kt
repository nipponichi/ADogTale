package com.pmdm.adogtale.model

import java.io.Serializable

data class LocalUser(
    var email: String,
    var username: String? = null,
    var town: String? = null,
    var phone: String? = null,
    var name: String? = null,
    var surname: String? = null,
    var password: String? = null,
) : Serializable