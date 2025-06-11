package com.base.app.baseapp.model

data class UserAccount(
    var name: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var sex: Boolean = true,
    var age: Int = 0,
    var state: Int = 0
)
