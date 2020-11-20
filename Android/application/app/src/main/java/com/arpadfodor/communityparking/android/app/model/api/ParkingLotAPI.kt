package com.arpadfodor.communityparking.android.app.model.api

import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiMetaData
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiReport
import com.arpadfodor.communityparking.android.app.model.api.dataclasses.ApiUser
import retrofit2.Call
import retrofit2.http.*

interface ParkingLotAPI {

    companion object {

        //emulator testing
        //const val BASE_URL = "http://10.0.2.2:8080/"
        const val BASE_URL = "https://www.mydomain.com/"

        const val API_URL = "${BASE_URL}api/v1/"

        const val GET_REPORTS = "${API_URL}reports/"
        const val GET_REPORTS_META = "${GET_REPORTS}meta/"
        const val POST_REPORT = "${API_URL}report/"
        const val LOGIN = "${API_URL}user/login"
        const val POST_API_USER = "${API_URL}api-user"
        const val PUT_SELF = "${API_URL}user/self"
        const val DELETE_SELF = "${API_URL}user/self"

    }

    @GET(GET_REPORTS)
    fun getReportsData(): Call<List<ApiReport>>

    @GET(GET_REPORTS_META)
    fun getReportsMeta(): Call<ApiMetaData>

    @POST(POST_REPORT)
    fun postReport(@Body report: ApiReport): Call<Void>

    @POST(LOGIN)
    fun login(): Call<Void>

    @POST(POST_API_USER)
    fun postApiUser(@Body user: ApiUser): Call<Void>

    @PUT(PUT_SELF)
    fun putSelf(@Body user: ApiUser): Call<Void>

    @DELETE(DELETE_SELF)
    fun deleteSelf(): Call<Void>

}