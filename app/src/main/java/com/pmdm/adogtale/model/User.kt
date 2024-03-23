package com.pmdm.adogtale.model

class User constructor(
    username: String,
    name: String,
    surname: String,
    email: String,
    password: String,
    phone: String
) {
    val username: String
    val name: String
    val surname: String
    val email: String
    val password: String
    val phone: String

    init {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}