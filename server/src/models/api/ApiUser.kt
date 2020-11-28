package hu.gyeben.communityparking.server.models.api

data class ApiUser(
    val email: String,
    var password: String,
    val name: String,
    val hint: String
)