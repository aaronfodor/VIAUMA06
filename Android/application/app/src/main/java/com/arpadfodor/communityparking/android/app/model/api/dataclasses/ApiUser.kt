package com.arpadfodor.communityparking.android.app.model.api.dataclasses

data class ApiUser(
    val email: String,
    val password: String,
    val name: String,
    val hint: String,
    val isActive: Boolean,
    val permissions:  List<Int>,
    val validFromUTC: String = "",
    val reservedLotId: String?
)