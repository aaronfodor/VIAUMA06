package hu.gyeben.communityparking.server.routes

import hu.gyeben.communityparking.server.models.api.ApiUser
import hu.gyeben.communityparking.server.models.db.User
import hu.gyeben.communityparking.server.models.db.toDbUser
import hu.gyeben.communityparking.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory

fun Route.userRouting() {
    val userService by di().instance<UserService>()

    route("/api/v1/user") {
        // Stores new user in DB
        post("register") {
            val user = call.receive<ApiUser>()
            user.password = BCrypt.hashpw(user.password, BCrypt.gensalt())

            if (userService.getUser(user.email) == null) {
                userService.addUser(user.toDbUser());
                call.respond(HttpStatusCode.Created)
            } else {
                call.respondText("Email is already registered", status = HttpStatusCode.Conflict)
            }
        }

        authenticate("userAuth") {
            //  Updates user in DB
            put("self") {
                val user = call.receive<ApiUser>()
                val principal = call.principal<UserIdPrincipal>()!!

                if (user.email != principal.name)
                    return@put call.respond(HttpStatusCode.BadRequest)

                if (userService.getUser(user.email) == null)
                    return@put call.respond(HttpStatusCode.NotFound)

                userService.updateUser(user.toDbUser())
                call.respond(HttpStatusCode.OK)
            }

            // Deactivates user
            delete("self") {
                val principal = call.principal<UserIdPrincipal>()!!
                val email = principal.name
                val user = userService.getUser(email)

                if (user == null)
                    return@delete call.respond(HttpStatusCode.NotFound)

                val deactivatedUser = User(user.email, user.password, user.name, user.hint, false)
                userService.updateUser(deactivatedUser)
                call.respond(HttpStatusCode.OK)
            }

            // Endpoint for checking user credentials
            post("login") {
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun Application.registerUserRoutes() {
    routing {
        userRouting()
    }
}