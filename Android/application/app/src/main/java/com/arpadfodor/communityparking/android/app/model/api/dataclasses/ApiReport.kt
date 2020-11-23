package com.arpadfodor.communityparking.android.app.model.api.dataclasses

data class ApiReport(
    val id: Int,
    val reporterEmail: String,
    val latitude: Double,
    val longitude: Double,
    val timestampUTC: String = "",
    val message: String = "",
    val reservedByEmail: String,
    val feePerHour: Double? = null,
    val imagePath: String = ""
)