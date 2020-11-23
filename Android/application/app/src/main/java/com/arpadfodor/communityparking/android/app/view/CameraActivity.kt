package com.arpadfodor.communityparking.android.app.view

import android.content.Intent
import android.hardware.SensorManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.OrientationEventListener
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.FrameLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.arpadfodor.communityparking.android.app.ApplicationRoot
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.AppActivity
import com.arpadfodor.communityparking.android.app.view.utils.overshootAppearingAnimation
import com.arpadfodor.communityparking.android.app.viewmodel.CameraViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_camera.*

class CameraActivity : AppActivity() {

    override lateinit var viewModel: CameraViewModel
    private lateinit var container: FrameLayout
    lateinit var deviceOrientationListener: OrientationEventListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_camera)
        container = findViewById(R.id.camera_container)
        val drawer = findViewById<DrawerLayout>(R.id.cameraActivityDrawerLayout)
        val navigation = findViewById<NavigationView>(R.id.camera_navigation)
        initUi(drawer, navigation)

        viewModel = ViewModelProvider(this).get(CameraViewModel::class.java)
        showCameraFragment()

        deviceOrientationListener = object : OrientationEventListener(this,
            SensorManager.SENSOR_DELAY_NORMAL) {

            override fun onOrientationChanged(orientation: Int) {
                CameraViewModel.deviceOrientation = orientation
            }

        }

    }

    override fun subscribeToViewModel() {
    }

    override fun subscribeListeners() {

        deviceOrientationListener.enable()

        /**
         * Before hiding the status bar, a wait is needed to let the UI settle.
         * Trying to set app to immersive mode before it's ready causes the flags not sticking.
         */
        container.postDelayed({
            container.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and SYSTEM_UI_FLAG_LAYOUT_STABLE)
            appearingAnimations()
        }, ApplicationRoot.IMMERSIVE_FLAG_TIMEOUT)

    }

    override fun unsubscribe() {
        deviceOrientationListener.disable()
    }

    private fun showCameraFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.camera_container, CameraFragment())
            .commit()
    }

    /**
     * When key down event is triggered, relay it via local broadcast so fragments can handle it
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        return when (keyCode){

            KeyEvent.KEYCODE_VOLUME_DOWN -> {

                val intent = Intent(CameraViewModel.KEY_EVENT_ACTION).apply {
                    putExtra(CameraViewModel.KEY_EVENT_EXTRA, keyCode)
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                true

            }
            else -> {
                super.onKeyDown(keyCode, event)
            }

        }

    }

    override fun appearingAnimations() {
        camera_switch_button?.overshootAppearingAnimation(this)
        camera_capture_button?.overshootAppearingAnimation(this)
    }

}
