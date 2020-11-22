package com.arpadfodor.communityparking.android.app.viewmodel

import androidx.lifecycle.MutableLiveData
import com.arpadfodor.communityparking.android.app.model.api.ApiService
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.repository.ReportRepository
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report
import com.arpadfodor.communityparking.android.app.viewmodel.utils.MasterDetailViewModel

class ReportViewModel : MasterDetailViewModel(){

    init {
        ReportRepository.getReports {
            reports.postValue(it)
        }
    }

    /**
     * List of report elements
     **/
    override val reports: MutableLiveData<List<Report>> by lazy {
        MutableLiveData<List<Report>>()
    }

    override fun sendReport(id: Int, callback: (Boolean) -> Unit){

        val report = reports.value?.find { it.id == id } ?: return

        val apiReport = ApiReport(report.id, report.reporterEmail, report.latitude, report.longitude,
            report.timestampUTC, report.message, report.reservingEmail, report.feePerHour, report.image)

        val success = {
            deselectRecognition()
        }
        val error = {
            callback(false)
        }

        ApiService.putReport(apiReport, success= success, error= error)

    }

    override fun deleteReport(id: Int, callback: (Boolean) -> Unit){

        val report = reports.value?.find { it.id == id } ?: return

        val success = {

            ReportRepository.deleteReport(id) { isSuccess ->

                if(isSuccess){
                    deselectRecognition()

                    val filteredAlerts = reports.value?.filter {
                        it.id != id
                    }
                    reports.postValue(filteredAlerts)
                    callback(true)
                }

            }

            callback(false)

        }
        val error = {
            callback(false)
        }

        ApiService.deleteReport(report.id, success= success, error= error)

    }

}