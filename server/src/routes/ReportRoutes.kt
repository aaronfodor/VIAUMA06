package hu.gyeben.communityparking.server.routes

import com.google.gson.Gson
import hu.gyeben.communityparking.server.model.api.ApiMetaData
import hu.gyeben.communityparking.server.model.api.ApiReport
import hu.gyeben.communityparking.server.models.api.ApiSearch
import hu.gyeben.communityparking.server.models.db.toDbReport
import hu.gyeben.communityparking.server.services.ReportService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun Route.reportRouting(uploadDir: String) {
    val reportService by di().instance<ReportService>()
    var modificationTimestamp: String = createTimestamp()

    route("/api/v1/report") {
        // Returns metadata that the client uses to decide whether to update or not
        get("meta") {
            val metadata = ApiMetaData("report", reportService.getReportCount(), modificationTimestamp)
            call.respond(metadata)
        }

        // Returns all of the reports
        get {
            call.respond(reportService.getAllAsApiReports())
        }

        // Serves images uploaded by users using the client
        static("/images") {
            files(uploadDir)
        }

        // Stores new report in DB and stores image in filesystem
        post {
            var report: ApiReport? = null
            var fileName: String? = null

            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name.equals("report")) {
                            report = Gson().fromJson(part.value, ApiReport::class.java)
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name.equals("image")) {
                            File(uploadDir).mkdirs();

                            fileName = "upload-${System.currentTimeMillis()}.png"
                            val file = File(uploadDir, fileName!!)

                            part.streamProvider()
                                .use { inputStream -> file.outputStream().buffered().use { inputStream.copyTo(it) } }
                        }
                    }
                }
                part.dispose()
            }

            if (report == null || fileName == null) {
                return@post call.respond(HttpStatusCode.BadRequest)
            } else {
                val uploadedImagePath = "/images/${fileName}"
                reportService.addReport(report!!.toDbReport(uploadedImagePath))
                modificationTimestamp = createTimestamp()
            }

            call.respond(HttpStatusCode.Created)
        }

        // TODO: permissions?
        // Updates report in DB
        put {
            val report = call.receive<ApiReport>()
            //val principal = call.principal<UserIdPrincipal>()!!

            //if (report.reporterEmail != principal.name)
            //    return@put call.respond(HttpStatusCode.BadRequest)

            if (reportService.getReport(report.id) == null)
                return@put call.respond(HttpStatusCode.NotFound)

            reportService.updateReport(report.id, report.toDbReport())
            modificationTimestamp = createTimestamp()
            call.respond(HttpStatusCode.OK)
        }

        // Returns "nearest" report
        post("search") {
            val searchParams = call.receive<ApiSearch>()
            val report = reportService.getNearestReport(searchParams.latitude, searchParams.longitude)

            if (report == null)
                return@post call.respond(HttpStatusCode.NotFound)

            call.respond(report)
        }
    }
}

fun Application.registerReportRoutes() {
    routing {
        reportRouting(environment.config.property("upload.dir").getString())
    }
}

fun createTimestamp(): String {
    return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC).format(Instant.now())
}