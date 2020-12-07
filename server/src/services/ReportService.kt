package hu.gyeben.communityparking.server.services

import hu.gyeben.communityparking.server.Utils
import hu.gyeben.communityparking.server.model.api.ApiReport
import hu.gyeben.communityparking.server.models.db.Report
import hu.gyeben.communityparking.server.models.db.ReportEntity
import org.jetbrains.exposed.sql.transactions.transaction

class ReportService {
    fun addReport(report: Report) = transaction {
        ReportEntity.new {
            this.reporterEmail = report.reporterEmail
            this.latitude = report.latitude
            this.longitude = report.longitude
            this.timestampUTC = report.timestampUTC
            this.message = report.message
            this.reservedByEmail = report.reservedByEmail
            this.feePerHour = report.feePerHour
            this.imagePath = report.imagePath
        }
    }

    fun getReport(id: Int): Report? = transaction {
        ReportEntity.findById(id)?.toReport()
    }

    fun getAllAsApiReports() = transaction {
        val result = mutableListOf<ApiReport>()
        ReportEntity.all().forEach { entity ->
            result.add(entity.toApiReport())
        }
        result
    }

    fun updateReport(id: Int, report: Report) = transaction {
        val reportEntity = ReportEntity.findById(id)
        reportEntity?.let {
            reportEntity.reporterEmail = report.reporterEmail
            reportEntity.latitude = report.latitude
            reportEntity.longitude = report.longitude
            reportEntity.timestampUTC = report.timestampUTC
            reportEntity.message = report.message
            reportEntity.reservedByEmail = report.reservedByEmail
            report.feePerHour?.let { reportEntity.feePerHour = report.feePerHour }
        }
    }

    fun getReportCount(): Int = transaction {
        ReportEntity.count().toInt()
    }

    fun getNearestReport(latitude: Double, longitude: Double): ApiReport? = transaction {
        var minDistance = Double.MAX_VALUE
        var report: ApiReport? = null

        ReportEntity.all().forEach {
            val distance = Utils.haversine(latitude, longitude, it.latitude, it.longitude)
            if (distance < minDistance) {
                minDistance = distance
                report = it.toApiReport()
            }
        }

        report
    }
}