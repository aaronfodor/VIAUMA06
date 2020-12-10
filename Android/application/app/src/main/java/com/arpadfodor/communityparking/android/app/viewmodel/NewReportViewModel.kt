package com.arpadfodor.communityparking.android.app.viewmodel

import android.graphics.Bitmap
import com.arpadfodor.communityparking.android.app.model.AccountService
import com.arpadfodor.communityparking.android.app.model.LocationService
import com.arpadfodor.communityparking.android.app.model.api.ApiService
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report
import com.arpadfodor.communityparking.android.app.viewmodel.utils.AppViewModel

class NewReportViewModel() : AppViewModel(){

    companion object{

        lateinit var report: Report
        lateinit var image: Bitmap

        fun setParameter(report: Report, image: Bitmap){
            this.report = report
            this.image = image
        }

    }

    val report = NewReportViewModel.report
    val image = NewReportViewModel.image

    fun sendReport(callback: (Boolean) -> Unit){

        val apiReport = ApiReport(report.id, report.reporterEmail, report.latitude, report.longitude,
            report.timestampUTC, report.message, report.reservedByEmail, report.feePerHour, report.imagePath)

        val success = {
            callback(true)
        }
        val error = {
            callback(false)
        }

        ApiService.postReport(image, apiReport, success = success, error = error)

    }

    fun updateRecognitionMessage(message: String, callback: (Boolean) -> Unit){
        report.message = message
        callback(true)
    }

    fun updateRecognitionPrice(feePerHour: Double?, callback: (Boolean) -> Unit){
        report.feePerHour = feePerHour
        callback(true)
    }

    fun reserveButtonClicked(callback: (Boolean, String) -> Unit){

        val userEmail = AccountService.userId

        var buttonText = ""
        var buttonEnabled = true

        when {
            AccountService.isCurrentAccountGuest() -> {
                buttonText = "Guest user cannot reserve"
                buttonEnabled = false
            }
            report.reservedByEmail == userEmail -> {
                buttonText = "Reserve"
                buttonEnabled = true
                report.reservedByEmail = ""
            }
            report.reservedByEmail.isNotEmpty() && report.reservedByEmail != userEmail -> {
                buttonText = "Already reserved"
                buttonEnabled = false
            }
            report.reservedByEmail.isEmpty() -> {
                buttonText = "Delete reservation"
                buttonEnabled = true
                report.reservedByEmail = userEmail
            }
        }

        callback(buttonEnabled, buttonText)

    }

    fun getAddressFromLocation(lat: Double, long: Double, callback: (String) -> Unit){
        return LocationService.getAddressFromLocation(lat, long, callback)
    }

    fun getUserEmail() : String {
        return AccountService.userId
    }

}