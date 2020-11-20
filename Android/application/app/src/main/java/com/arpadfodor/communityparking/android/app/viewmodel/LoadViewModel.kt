package com.arpadfodor.communityparking.android.app.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.lifecycle.MutableLiveData
import com.arpadfodor.communityparking.android.app.model.ImageConverter
import com.arpadfodor.communityparking.android.app.model.MediaHandler
import com.arpadfodor.communityparking.android.app.model.MetaProvider
import com.arpadfodor.communityparking.android.app.viewmodel.utils.AppViewModel

class LoadViewModel : AppViewModel(){

    companion object{
        const val GALLERY_REQUEST_CODE = 2
        var screenDimensions = Size(0, 0)
    }

    val imageMimeTypes = arrayListOf("image/jpeg", "image/png")

    /**
     * The loaded image to feed
     **/
    val loadedImage: MutableLiveData<Bitmap> by lazy {
        MutableLiveData<Bitmap>()
    }

    /**
     * Metadata of the loaded image
     * [0]: date of image taking
     * [1]: latitude of the photo origin
     * [2]: longitude of the photo origin
     **/
    val imageMetaData: MutableLiveData<Array<String>> by lazy {
        MutableLiveData<Array<String>>()
    }

    fun loadImage(selectedImageUri: Uri, callback: (Boolean) -> Unit){

        Thread {
            val sourceBitmap = MediaHandler.getImageByUri(selectedImageUri)
            sourceBitmap ?: callback(false)

            sourceBitmap?.let {
                val imageOrientation = MediaHandler.getPhotoOrientation(selectedImageUri)
                val imageMetaInfo = MetaProvider.getImageMetaData(selectedImageUri)

                imageMetaData.postValue(imageMetaInfo)

                val rotatedBitmap = ImageConverter.rotateBitmap(it, imageOrientation)
                loadedImage.postValue(rotatedBitmap)
            }

        }.start()

    }

    fun rotateImage(){
        val sourceBitmap = loadedImage.value ?: return
        Thread {
            val rotatedBitmap = ImageConverter.rotateBitmap(sourceBitmap, 90)
            loadedImage.postValue(rotatedBitmap)
        }.start()
    }

    fun setScreenProperties(width: Int, height: Int){
        screenDimensions = Size(width, height)
    }

}