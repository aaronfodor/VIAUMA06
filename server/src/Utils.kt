package hu.gyeben.communityparking.server

object Utils {
    val earthR = 6371

    fun haversine(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Double {
        val dLatitude = Math.toRadians(latitude2 - latitude1)
        val dLongitude = Math.toRadians(longitude2 - longitude1)
        val rLatitude1 = Math.toRadians(latitude1)
        val rLatitude2 = Math.toRadians(latitude2)

        val a = Math.pow(Math.sin(dLatitude / 2), 2.0) + Math.pow(Math.sin(dLongitude / 2), 2.0) * Math.cos(rLatitude1) * Math.cos(rLatitude2)
        val c = 2 * Math.asin(Math.sqrt(a))
        return earthR * c
    }
}