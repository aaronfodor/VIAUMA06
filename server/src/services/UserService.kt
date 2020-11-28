package hu.gyeben.communityparking.server.services

import hu.gyeben.communityparking.server.models.db.User
import hu.gyeben.communityparking.server.models.db.UserEntity
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
        UserEntity.findById(email)?.toUser()
    }

    fun updateUser(user: User) = transaction {
        val userEntity = UserEntity.findById(user.email)
        userEntity?.let {
            userEntity.email = user.email
            userEntity.password = user.password
            userEntity.name = user.name
            userEntity.hint = user.hint
            userEntity.isActive = user.isActive
        }
    }
}