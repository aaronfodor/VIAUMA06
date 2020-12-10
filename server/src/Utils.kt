package hu.gyeben.communityparking.server

import kotlin.math.*

object Utils {
    private const val earthR = 6371

    fun haversine(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
        val dLatitude = Math.toRadians(latitude2 - latitude1)
        val dLongitude = Math.toRadians(longitude2 - longitude1)
        val rLatitude1 = Math.toRadians(latitude1)
        val rLatitude2 = Math.toRadians(latitude2)

        val a = sin(dLatitude / 2).pow(2.0) + sin(dLongitude / 2).pow(2.0) * cos(rLatitude1) * cos(rLatitude2)
        val c = 2 * asin(sqrt(a))
        return earthR * c
    }
}