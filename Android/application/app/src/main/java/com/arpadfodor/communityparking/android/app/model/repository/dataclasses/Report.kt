package com.arpadfodor.communityparking.android.app.model.repository.dataclasses

import android.graphics.Bitmap

data class Report(
    val id: Int = 0,
    val reporterEmail: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestampUTC: String = "",
    var message: String = "",
    var reservingEmail: String = "",
    var feePerHour: Double? = null,
    val image: Bitmap?
)