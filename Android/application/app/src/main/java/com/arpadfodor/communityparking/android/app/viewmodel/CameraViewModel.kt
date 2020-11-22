package com.arpadfodor.communityparking.android.app.viewmodel

import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.lifecycle.MutableLiveData
import com.arpadfodor.communityparking.android.app.model.AccountService
import com.arpadfodor.communityparking.android.app.model.MediaHandler
import com.arpadfodor.communityparking.android.app.model.MetaProvider
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report
import com.arpadfodor.communityparking.android.app.viewmodel.utils.AppViewModel
import java.io.OutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraViewModel : AppViewModel(){

    companion object{

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        var KEY_EVENT_ACTION = ""
        var KEY_EVENT_EXTRA = ""

        var deviceOrientation: Int = 0
            // clustered device orientations - value can be 0, 90, 180, 270
            get() {
                var roundedOrientation = 0

                if(315 < field || field <= 45){
                    roundedOrientation = 0
                }
                else if(field in 46..135){
                    roundedOrientation = 90
                }
                else if(field in 136..225){
                    roundedOrientation = 180
                }
                else if(field in 226..315){
                    roundedOrientation = 270
                }

                return roundedOrientation
            }

        var screenDimensions = Size(0, 0)

        /**
         * The current image
         **/
        var currentImage: Bitmap? = null

    }

    var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    /**
     * List of reports
     **/
    val recognitions: MutableLiveData<Array<Report>> by lazy {
        MutableLiveData<Array<Report>>()
    }

    /**
     *  [androidx.camera.core.ImageAnalysisConfig] requires enum value of [androidx.camera.core.AspectRatio].
     *  Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *
     *  @return suitable aspect ratio
     **/
    fun aspectRatio(width: Int, height: Int): Int {

        val previewRatio = max(width, height).toDouble() / min(width, height)

        return if(abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)){
            AspectRatio.RATIO_4_3
        }
        else{
            AspectRatio.RATIO_16_9
        }

    }

    fun setScreenProperties(width: Int, height: Int){
        screenDimensions = Size(width, height)
    }

    fun getImageOutputStream(): OutputStream? {
        return MediaHandler.getImagePublicDirOutputStream()
    }

    private fun prepareRecognitions() : List<Report> {

        val imageMeta = MetaProvider.getDeviceMetaData()

        val recognitions = arrayListOf<Report>()
        val user = AccountService.userId

        recognitions.add(
            Report(
                id = 1,
                reporterEmail = user,
                latitude = imageMeta[1].toDouble(),
                longitude = imageMeta[2].toDouble(),
                timestampUTC = imageMeta[0],
                message = "",
                reservingEmail = "",
                feePerHour = null,
                image = currentImage)
        )

        this.recognitions.postValue(recognitions.toTypedArray())
        return recognitions

    }

    fun setAlertActivityParams(){
        val items = prepareRecognitions()
        NewReportViewModel.setParameter(items)
    }

}