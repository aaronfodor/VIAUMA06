package hu.gyeben.communityparking.server.models.db

import hu.gyeben.communityparking.server.models.api.ApiUser
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Users : IntIdTable() {
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val name = varchar("name", 255)
    val hint = varchar("hint", 255)
    val isActive = bool("isActive")
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var email by Users.email
    var password by Users.password
    var name by Users.name
    var hint by Users.hint
    var isActive by Users.isActive

    /*var permissions by Users.permissions
    var validFromUTC by Users.validFromUTC*/

    override fun toString(): String = "User($email, $name)"

    fun toUser() = User(email, password, name, hint, isActive)
}

data class User(
    val email: String,
    val password: String,
    val name: String,
    val hint: String,
    val isActive: Boolean
    /*val permissions: String,
    val validFromUTC: String = ""*/
)

fun ApiUser.toDbUser(isActive: Boolean = true): User {
    return User(email, password, name, hint, isActive)
}