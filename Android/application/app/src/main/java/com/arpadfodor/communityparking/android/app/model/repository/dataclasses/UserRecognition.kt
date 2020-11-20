package com.arpadfodor.communityparking.android.app.model.repository.dataclasses

import android.graphics.Bitmap

data class UserRecognition(
    val id: Int = 0,
    val reporterEmail: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestampUTC: String = "",
    var message: String = "",
    var isReserved: Boolean = false,
    var feePerHour: Int = 0,
    var image: Bitmap?,
    var isSelected: Boolean,
    var isSent: Boolean,
    )