package com.arpadfodor.communityparking.android.app.model.repository.dataclasses

data class Report(
    val id: Int = 0,
    val reporterEmail: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestampUTC: String = "",
    var message: String = "",
    var reservedByEmail: String = "",
    var feePerHour: Double? = null,
    val imagePath: String
)