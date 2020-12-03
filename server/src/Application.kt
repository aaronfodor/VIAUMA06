package hu.gyeben.communityparking.server

import hu.gyeben.communityparking.server.routes.registerReportRoutes
import hu.gyeben.communityparking.server.routes.registerUserRoutes
import hu.gyeben.communityparking.server.services.UserService
import hu.gyeben.communityparking.server.services.bindServices
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.errors.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    initDatabase()

    install(ContentNegotiation) { gson {} }
    install(CallLogging)
    install(StatusPages) {
        exception<EntityNotFoundException> {
            call.respond(HttpStatusCode.NotFound)
        }
        exception<IllegalStateException> {
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<IOException> {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    di {
        bindServices()
    }

    install(Authentication) {
        basic("userAuth") {
            realm = "ktor"
            validate { authenticate(it) }
        }
    }

    registerReportRoutes()
    registerUserRoutes()
}

fun Application.authenticate(credential: UserPasswordCredential): Principal? {
    val userService by di().instance<UserService>()
    val dbUser = userService.getUser(credential.name)

    if (dbUser == null || !dbUser.isActive) {
        return null
    }
    val passwordHash = dbUser.password

    if (BCrypt.checkpw(credential.password, passwordHash))
        return UserIdPrincipal(credential.name)

    return null
}