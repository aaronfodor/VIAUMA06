package hu.gyeben.communityparking.server.model.api

data class ApiMetaData(
    var tableId: String,
    var dataSize: Int,
    var modificationTimeStampUTC: String
)