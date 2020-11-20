package com.arpadfodor.communityparking.android.app.model.api.dataclasses

import android.graphics.Bitmap

data class ApiReport(
    val id: Int,
    val reporterEmail: String,
    val latitude: Double,
    val longitude: Double,
    val timestampUTC: String = "",
    val message: String = "",
    val isReserved: Boolean,
    val feePerHour: Int,
    val image: Bitmap?
)