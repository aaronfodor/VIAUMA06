package com.arpadfodor.communityparking.android.app.viewmodel

import androidx.lifecycle.MutableLiveData
import com.arpadfodor.communityparking.android.app.model.api.ApiService
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report
import com.arpadfodor.communityparking.android.app.viewmodel.utils.MasterDetailViewModel

class NewReportViewModel : MasterDetailViewModel(){

    companion object{

        private var listParam = listOf<Report>()

        /**
         * Use it to pass list parameter to an instance of this activity before starting it.
         * Used because passing custom objects between activities can be problematic via intents.
         **/
        fun setParameter(list: List<Report>){
            listParam = list
        }

    }

    /**
     * List of recognition elements
     **/
    override val reports: MutableLiveData<List<Report>> by lazy {
        MutableLiveData<List<Report>>(listParam)
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

        ApiService.postReport(apiReport, success = success, error = error)

    }

}