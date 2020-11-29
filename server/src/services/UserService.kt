package hu.gyeben.communityparking.server.services

import hu.gyeben.communityparking.server.models.db.User
import hu.gyeben.communityparking.server.models.db.UserEntity
import hu.gyeben.communityparking.server.models.db.Users
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.email = user.email
            this.password = user.password
            this.name = user.name
            this.hint = user.hint
            this.isActive = user.isActive
        }
    }

    fun getUser(email: String): User? = transaction {
        val users = UserEntity.find { Users.email eq email }

        if (users.empty())
            null
        else
            users.first().toUser()
    }

    fun updateUser(user: User) = transaction {
        val users = UserEntity.find { Users.email eq user.email }
        val userEntity = if (users.empty()) null else users.first()

        userEntity?.let {
            userEntity.email = user.email
            userEntity.password = user.password
            userEntity.name = user.name
            userEntity.hint = user.hint
            userEntity.isActive = user.isActive
        }
    }
}