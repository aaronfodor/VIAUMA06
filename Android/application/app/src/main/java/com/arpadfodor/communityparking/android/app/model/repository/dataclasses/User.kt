package com.arpadfodor.communityparking.android.app.model.repository.dataclasses

data class User(
    var email: String = "",
    val password: String = "",
    var name: String = "",
    val hint: String = "",
    var reservedLotId: String = ""
)