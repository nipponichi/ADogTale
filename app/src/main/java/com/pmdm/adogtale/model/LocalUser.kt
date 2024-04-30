package com.pmdm.adogtale.model

import java.io.Serializable

<<<<<<< Updated upstream
data class LocalUser(
    var email: String,
    var username: String? = null,
    var town: String? = null,
    var phone: String? = null,
    var name: String? = null,
    var surname: String? = null,
    var password: String? = null,
=======
data class LocalUser (
    var email: String = "",
    var username: String = "",
    var town: String = "",
    var phone: String = "",
    var name: String = "",
    var surname: String = "",
    var password: String = "",
    var token: String = "",
>>>>>>> Stashed changes
) : Serializable