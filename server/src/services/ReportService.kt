package hu.gyeben.communityparking.server.services

import hu.gyeben.communityparking.server.models.db.Report
import hu.gyeben.communityparking.server.models.db.ReportEntity
import hu.gyeben.communityparking.server.models.db.Reports
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

    fun updateReport(report: Report) = transaction {
        val reportEntity = ReportEntity.findById(report.id)
        reportEntity?.let {
            reportEntity.reporterEmail = report.reporterEmail
            reportEntity.latitude = report.latitude
            reportEntity.longitude = report.longitude
            reportEntity.timestampUTC = report.timestampUTC
            reportEntity.message = report.message
            reportEntity.reservedByEmail = report.reservedByEmail
            reportEntity.feePerHour = report.feePerHour
            reportEntity.imagePath = report.imagePath
        }
    }
}