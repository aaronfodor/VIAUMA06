package com.arpadfodor.communityparking.android.app

import android.Manifest
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import com.arpadfodor.communityparking.android.app.model.*
import com.arpadfodor.communityparking.android.app.model.api.ApiService
import com.arpadfodor.communityparking.android.app.model.repository.GeneralRepository
import com.arpadfodor.communityparking.android.app.viewmodel.CameraViewModel
import java.util.*

class ApplicationRoot : Application() {

    companion object{

        private const val TAG = "Application Root"
        const val IMMERSIVE_FLAG_TIMEOUT = 100L

        var requiredPermissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_MEDIA_LOCATION)
        }
        else{
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        }

        var isAutoSyncEnabled = true
        var keepScreenAlive = true

    }

    /**
     * This method fires only once per application start, getApplicationContext returns null here
     **/
    init {
        Log.i(TAG, "Constructor fired")
    }

    /**
     * This method fires once as well as the constructor, but also application has context here
     **/
    override fun onCreate() {

        super.onCreate()
        Log.i(TAG, "onCreate fired")

        //init model singletons
        GeneralRepository.initialize(applicationContext)
        ApiService.initialize(AccountService.getClient())

        val appName = getString(R.string.app_name)
        MediaHandler.initialize(applicationContext, appName)
        LocationService.initialize(applicationContext)
        TextToSpeechService.init(applicationContext)

        CameraViewModel.KEY_EVENT_ACTION = getString(R.string.KEY_EVENT_ACTION)
        CameraViewModel.KEY_EVENT_EXTRA = getString(R.string.KEY_EVENT_EXTRA)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        isAutoSyncEnabled = preferences.getBoolean(getString(R.string.SETTINGS_AUTO_SYNC),
            resources.getBoolean(R.bool.settings_auto_sync_default))

        keepScreenAlive = preferences.getBoolean(getString(R.string.SETTINGS_KEEP_SCREEN_ALIVE),
            resources.getBoolean(R.bool.settings_keep_screen_alive_default))

        if(isAutoSyncEnabled){

            GeneralRepository.updateAll{ isReportsSuccess ->

                if(isReportsSuccess){
                    val currentTime = Calendar.getInstance().time.toString()
                    preferences.edit().putString(getString(R.string.LAST_SYNCED_DB_REPORTS), currentTime)
                        .apply()
                }

            }

        }

    }

}