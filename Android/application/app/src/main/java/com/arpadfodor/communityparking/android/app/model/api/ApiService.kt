package com.arpadfodor.communityparking.android.app.model.api

import android.net.Uri
import com.arpadfodor.communityparking.android.app.model.DateHandler
import com.arpadfodor.communityparking.android.app.model.api.CommunityParkingAPI.Companion.MULTIPART_FORM_DATA
import com.arpadfodor.communityparking.android.app.model.api.CommunityParkingAPI.Companion.PHOTO_MULTIPART_KEY_IMG
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiMetaData
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiUser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object ApiService{

    private lateinit var parkingLotAPI: CommunityParkingAPI

    fun initialize(httpClient: OkHttpClient){
        val retrofitStolenVehiclesAPI = Retrofit.Builder()
            .baseUrl(CommunityParkingAPI.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        this.parkingLotAPI = retrofitStolenVehiclesAPI.create(CommunityParkingAPI::class.java)
    }

    fun getReportsData(callback: (List<ApiReport>) -> Unit) {

        Thread {

            var dataResponse: List<ApiReport> = listOf()

            try {
                val dataCall = parkingLotAPI.getReportsData()
                dataResponse = dataCall.execute().body() ?: emptyList()

                //TODO: JUST FOR TESTING
                val r1 = ApiReport(1, "cecil@bela.com", 10.0, 11.0, "2020-06-30", "OMG!!!", "sanyi@evi.hu", 300.0, null)
                val r2 = ApiReport(2, "bela@beno.com", 10.0, 12.0, "2020-06-22", "gggg!!!", "cecil@bela.com", 300.0, null)
                val r3 = ApiReport(3, "bela@beno.com", 30.0, 11.0, "2020-06-20", "wwww!!!", "", 300.0, null)
                dataResponse = listOf(r1, r2, r3)
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
            //var timestampUTC = DateHandler.dateToString(DateHandler.defaultDate())
            //TODO: REPLACE THIS, JUST FOR TESTING
            var timestampUTC = DateHandler.currentTimeUTC()

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

    fun postReport(report: ApiReport, success: () -> Unit, error: () -> Unit){

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
                if(isSuccess) {
                    success()
                }
                else{
                    error()
                }
            }

        }.start()

    }

    fun putReport(report: ApiReport, success: () -> Unit, error: () -> Unit){

        Thread {

            var isSuccess = false

            try {
                val postReportCall = parkingLotAPI.putReport(report)
                val responseCode = postReportCall.execute().code()
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

    fun deleteReport(reportId: Int, success: () -> Unit, error: () -> Unit){

        Thread {

            var isSuccess = false

            try {
                val deleteReportCall = parkingLotAPI.deleteReport(reportId)
                val responseCode = deleteReportCall.execute().code()
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

    fun uploadImage(fileUri: Uri, name: String, description: String, success: () -> Unit, error: () -> Unit) {

        val file = File(fileUri.path)
        val requestFile = file.asRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData(PHOTO_MULTIPART_KEY_IMG, file.name, requestFile)

        val nameParam = name.toRequestBody(MultipartBody.FORM)
        val descriptionParam = description.toRequestBody(MultipartBody.FORM)

        val uploadImageRequest = parkingLotAPI.uploadImage(body, nameParam, descriptionParam)

        Thread {
            var isSuccess = false

            try {
                val call = parkingLotAPI.login()
                val responseCode = call.execute().code()
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