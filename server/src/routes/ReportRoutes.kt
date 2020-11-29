package hu.gyeben.communityparking.server.routes

import hu.gyeben.communityparking.server.model.api.ApiMetaData
import hu.gyeben.communityparking.server.model.api.ApiReport
import hu.gyeben.communityparking.server.models.db.toDbReport
import hu.gyeben.communityparking.server.services.ReportService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.format.DateTimeFormatter

fun Route.reportRouting(uploadDir: String) {
    val reportService by di().instance<ReportService>()
    var modificationTimestamp: String = ""

    // TODO: implement route handling
    route("/report") {
        post {
            LoggerFactory.getLogger(Application::class.simpleName).debug(" --- important --- POST /REPORT !!! --- ")
            // TODO: implement receiving multipart request
            val report = call.receive<ApiReport>()
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                LoggerFactory.getLogger(Application::class.simpleName).info("Received part: ${part.name}")
            }

            modificationTimestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        }

        put {
            val report = call.receive<ApiReport>()
            //val principal = call.principal<UserIdPrincipal>()!!

            //if (report.reporterEmail != principal.name)
            //    return@put call.respond(HttpStatusCode.BadRequest)

            if (reportService.getReport(report.id) == null)
                return@put call.respond(HttpStatusCode.NotFound)

            reportService.updateReport(report.toDbReport())
            modificationTimestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            call.respond(HttpStatusCode.OK)
        }

        get("meta") {
            val metadata = ApiMetaData("report", 0, modificationTimestamp)
            call.respond(metadata)
        }

        get {
            call.respond(reportService.getAllReports())
        }   

        authenticate("userAuth") {
        }
    }
}

fun Application.registerReportRoutes() {
    routing {
        reportRouting(environment.config.property("upload.dir").getString())
    }
}