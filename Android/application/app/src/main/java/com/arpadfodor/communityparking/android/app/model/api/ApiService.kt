package com.arpadfodor.communityparking.android.app.model.api

import com.arpadfodor.communityparking.android.app.model.DateHandler
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiMetaData
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiUser
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object ApiService{

    private lateinit var parkingLotAPI: ParkingLotAPI

    fun initialize(httpClient: OkHttpClient){
        val retrofitStolenVehiclesAPI = Retrofit.Builder()
            .baseUrl(ParkingLotAPI.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        this.parkingLotAPI = retrofitStolenVehiclesAPI.create(ParkingLotAPI::class.java)
    }

    fun getReportsData(callback: (List<ApiReport>) -> Unit) {

        Thread {

            var dataResponse: List<ApiReport> = listOf()

            try {
                val dataCall = parkingLotAPI.getReportsData()
                dataResponse = dataCall.execute().body() ?: emptyList()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(dataResponse)
            }

        }.start()

    }

    fun getReportsMeta(callback: (Int, String) -> Unit) {

        Thread {

            var size = 0
            var timestampUTC = DateHandler.dateToString(DateHandler.defaultDate())

            try {
                val metaDataCall = parkingLotAPI.getReportsMeta()
                val metaDataResponse = metaDataCall.execute().body()
                    ?: ApiMetaData("", 0, timestampUTC)
                size = metaDataResponse.dataSize
                timestampUTC = metaDataResponse.modificationTimeStampUTC
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(size, timestampUTC)
            }

        }.start()

    }

    fun postReport(report: ApiReport, callback: (Boolean) -> Unit){

        Thread {

            var isSuccess = false

            try {
                val postReportCall = parkingLotAPI.postReport(report)
                val responseCode = postReportCall.execute().code()
                isSuccess = responseCode < 300
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(isSuccess)
            }

        }.start()

    }

    fun deleteSelf(success: () -> Unit, error: () -> Unit){

        Thread {

            var isSuccess = false

            try {
                val deleteSelfCall = parkingLotAPI.deleteSelf()
                val responseCode = deleteSelfCall.execute().code()
                isSuccess = responseCode < 300
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                if(isSuccess) {
                    success()
                }
                else{
                    error()
                }
            }

        }.start()

    }

    fun postApiUser(email: String, name: String, password: String, success: () -> Unit, error: () -> Unit){

        Thread {

            var isSuccess = false

            try {
                val user = ApiUser(email, password, name, "", true, listOf(), "", "")
                val postApiUserCall = parkingLotAPI.postApiUser(user)
                val responseCode = postApiUserCall.execute().code()
                isSuccess = responseCode < 300
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                if(isSuccess) {
                    success()
                }
                else{
                    error()
                }
            }

        }.start()

    }

    fun putSelf(email: String, nameToSet: String, passwordToSet: String, success: () -> Unit, error: () -> Unit){

        Thread {

            var isSuccess = false

            try {
                val user = ApiUser(email, passwordToSet, nameToSet, "", true, listOf(), "", "")
                val putSelfCall = parkingLotAPI.putSelf(user)
                val responseCode = putSelfCall.execute().code()
                isSuccess = responseCode < 300
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                if(isSuccess) {
                    success()
                }
                else{
                    error()
                }
            }

        }.start()

    }

    fun login(success: () -> Unit, error: () -> Unit){

        Thread {
            var isSuccess = false

            try {
                val metaDataCall = parkingLotAPI.login()
                val responseCode = metaDataCall.execute().code()
                isSuccess = responseCode < 300
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                if(isSuccess){
                    success()
                }
                else{
                    error()
                }
            }

        }.start()

    }

}