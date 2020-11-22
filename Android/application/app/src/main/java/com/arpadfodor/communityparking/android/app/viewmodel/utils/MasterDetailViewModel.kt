package com.arpadfodor.communityparking.android.app.viewmodel.utils

import androidx.lifecycle.MutableLiveData
import com.arpadfodor.communityparking.android.app.model.AccountService
import com.arpadfodor.communityparking.android.app.model.LocationService
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report

abstract class MasterDetailViewModel : AppViewModel(){

    /**
     * List of recognition elements
     **/
    open val reports: MutableLiveData<List<Report>> by lazy {
        MutableLiveData<List<Report>>()
    }

    /**
     * Whether or not the UI is in two-pane mode, i.e. running on a tablet device
     */
    var twoPane = false

    /**
     * The current, selected recognition
     **/
    val selectedRecognitionId: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    /**
     * Whether to show details fragment
     **/
    val showDetails: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    open fun sendReport(id: Int, callback: (Boolean) -> Unit){}

    open fun deleteReport(id: Int, callback: (Boolean) -> Unit){

        deselectRecognition()

        val filteredAlerts = reports.value?.filter {
            it.id != id
        }
        reports.postValue(filteredAlerts)

        callback(true)

    }

    fun selectRecognition(id: Int) {
        selectedRecognitionId.postValue(id)
        showDetails.postValue(true)
    }

    fun deselectRecognition() {
        selectedRecognitionId.postValue(0)
        showDetails.postValue(false)
    }

    fun getRecognitionById(id: Int) : Report?{
        return reports.value?.find { it.id == id }
    }

    open fun updateRecognitionMessage(id: Int, message: String, callback: (Boolean) -> Unit){

        val recognitionList = reports.value ?: return
        recognitionList.forEach {
            if(it.id == id){
                it.message = message
            }
        }
        reports.postValue(recognitionList)
        callback(true)

    }

    open fun updateRecognitionPrice(id: Int, feePerHour: Double?, callback: (Boolean) -> Unit){

        val recognitionList = reports.value ?: return
        recognitionList.forEach {
            if(it.id == id){
                it.feePerHour = feePerHour
            }
        }
        reports.postValue(recognitionList)
        callback(true)

    }

    open fun reserveButtonClicked(id: Int, callback: (Boolean, String) -> Unit){

        val recognitionList = reports.value ?: return
        val userEmail = AccountService.userId

        var buttonText = ""
        var buttonEnabled = true

        recognitionList.forEach {
            if(it.id == id){

                when {
                    AccountService.isCurrentAccountGuest() -> {
                        buttonText = "Guest user cannot reserve"
                        buttonEnabled = false
                    }
                    it.reservingEmail == userEmail -> {
                        buttonText = "Reserve"
                        buttonEnabled = true
                        it.reservingEmail = ""
                    }
                    it.reservingEmail.isNotEmpty() && it.reservingEmail != userEmail -> {
                        buttonText = "Already reserved"
                        buttonEnabled = false
                    }
                    it.reservingEmail.isEmpty() -> {
                        buttonText = "Delete reservation"
                        buttonEnabled = true
                        it.reservingEmail = userEmail
                    }
                }

            }
        }

        reports.postValue(recognitionList)
        callback(buttonEnabled, buttonText)

    }

    fun getAddressFromLocation(lat: Double, long: Double, callback: (String) -> Unit){
        return LocationService.getAddressFromLocation(lat, long, callback)
    }

    fun getUserEmail() : String {
        return AccountService.userId
    }

}