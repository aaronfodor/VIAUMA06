package hu.gyeben.communityparking.server.models.api

data class ApiMetaData(
    var tableId: String,
    var dataSize: Int,
    var modificationTimeStampUTC: String
)