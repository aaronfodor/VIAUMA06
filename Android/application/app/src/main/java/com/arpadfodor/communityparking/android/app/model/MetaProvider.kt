package com.arpadfodor.communityparking.android.app.model

import android.net.Uri

object MetaProvider {

    /**
     * Metadata
     * [0]: date of image taking / device date
     * [1]: latitude of the photo origin / device latitude
     * [2]: longitude of the photo origin / device longitude
     **/

    fun getImageMetaData(photoUri: Uri): Array<String> {
        return MediaHandler.getImageMeta(photoUri)
    }

    fun getDeviceMetaData(resultCallback: (Array<String>) -> Unit){
        val date = DateHandler.currentTimeUTC()
        LocationService.getLocation{ location ->
            val results = arrayOf(date, location[0].toString(), location[1].toString())
            resultCallback(results)
        }
    }

}