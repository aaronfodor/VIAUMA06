package hu.gyeben.communityparking.server.model.api

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