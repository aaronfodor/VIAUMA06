package com.arpadfodor.communityparking.android.app.model.api

import android.graphics.Bitmap
import android.media.Image
import com.arpadfodor.communityparking.android.app.model.DateHandler
import com.arpadfodor.communityparking.android.app.model.api.CommunityParkingAPI.Companion.MULTIPART_FORM_DATA
import com.arpadfodor.communityparking.android.app.model.api.CommunityParkingAPI.Companion.MULTIPART_IMAGE_KEY
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiCoordinate
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiMetaData
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiUser
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

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
                val r1 = ApiReport(
                    1,
                    "cecil@bela.com",
                    19.0,
                    31.0,
                    "2020-06-30",
                    "Few places left",
                    "sanyi@evi.hu",
                    350.0,
                    "https://news.blr.com/app/uploads/sites/2/2019/05/Parking-lot.jpg"
                )
                val r2 = ApiReport(
                    2,
                    "bela@beno.com",
                    10.0,
                    12.0,
                    "2020-06-22",
                    "My favourite",
                    "cecil@bela.com",
                    0.0,
                    "https://www.kai-pavement.com/files/img_84461319227780.jpg"
                )
                val r3 = ApiReport(
                    3,
                    "bela@beno.com",
                    30.0,
                    16.0,
                    "2020-06-20",
                    "Nice place to park!",
                    "",
                    300.0,
                    "https://xpatloop.com/binaries/content/gallery/2019-photos/getting-around/03/parking-427955_960_720.jpg"
                )
                val r4 = ApiReport(
                    4,
                    "bela@beno.com",
                    40.0,
                    13.0,
                    "2020-06-28",
                    "Great place!",
                    "",
                    null,
                    "https://assets.bwbx.io/images/users/iqjWHBFdfxIU/intTFNX2AHxk/v0/1000x-1.jpg"
                )
                val r5 = ApiReport(
                    5,
                    "bela@beno.com",
                    40.0,
                    13.0,
                    "2020-06-28",
                    "Great place!",
                    "",
                    null,
                    "https://auto.com/r.jpg"
                )
                dataResponse = listOf(r1, r2, r3, r4, r5)
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

    fun getClosestReportToLocation(latitude: Double, longitude: Double, callback: (ApiReport) -> Unit) {

        Thread {

            var dataResponse = ApiReport(0, "", 0.0, 0.0,
                "", "", "", 0.0, "")

            try {
                val coordinate = ApiCoordinate(latitude, longitude)
                val closestReportCall = parkingLotAPI.getClosestReportToLocation(coordinate)
                val response = closestReportCall.execute().body()
                dataResponse = response ?: dataResponse
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(dataResponse)
            }

        }.start()

    }

    fun postReport(bitmap: Bitmap?, report: ApiReport, success: () -> Unit, error: () -> Unit){

        Thread {

            var isSuccess = false

            var byteArray = ByteArray(0)

            bitmap?.let { bitmap->
                ByteArrayOutputStream().use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    byteArray = it.toByteArray()
                }
            }

            val request = byteArray.toRequestBody(MULTIPART_FORM_DATA.toMediaTypeOrNull())
            val multipartBodyImage = MultipartBody.Part.createFormData(MULTIPART_IMAGE_KEY, MULTIPART_IMAGE_KEY, request)

            try {
                val postReportCall = parkingLotAPI.postReport(multipartBodyImage, report)
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
                val user = ApiUser(email, password, name, "", true, listOf(), "")
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
                val user = ApiUser(email, passwordToSet, nameToSet, "", true, listOf(), "")
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