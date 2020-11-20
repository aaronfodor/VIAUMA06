package com.arpadfodor.communityparking.android.app.model.repository.dataclasses

import android.graphics.Bitmap

data class Report(
    val id: String = "",
    val reporterEmail: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestampUTC: String = "",
    val message: String = "",
    val isReserved: Boolean = false,
    val feePerHour: Int = 0,
    val image: Bitmap?,
    var isSelected: Boolean
)