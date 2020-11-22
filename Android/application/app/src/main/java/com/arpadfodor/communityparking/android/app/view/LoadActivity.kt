package com.arpadfodor.communityparking.android.app.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arpadfodor.communityparking.android.app.ApplicationRoot
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.viewmodel.LoadViewModel
import com.arpadfodor.communityparking.android.app.view.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_load.*

class LoadActivity : AppActivity() {

    override lateinit var viewModel: LoadViewModel
    private lateinit var container: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_load)
        container = findViewById(R.id.loaded_image_container)
        val drawer = findViewById<DrawerLayout>(R.id.loadActivityDrawerLayout)
        val navigation = findViewById<NavigationView>(R.id.load_navigation)
        viewModel = ViewModelProvider(this).get(LoadViewModel::class.java)
        initUi(drawer, navigation)

        //due to an Android bug, setting clip to outline cannot be done from XML
        ivLoadedImage.clipToOutline = true

        extendedFabLoadAction.setOnClickListener {
            loadImage()
        }

        extendedFabLoadAction.text = getString(R.string.load_an_image)
        extendedFabLoadAction.icon = ContextCompat.getDrawable(this, R.drawable.icon_photo_library)
        extendedFabLoadAction.iconTint = ContextCompat.getColorStateList(this, R.color.selector_ic)
        extendedFabLoadAction.backgroundTintList = ContextCompat.getColorStateList(this, R.color.selector_fab_normal_color)

    }

    override fun onResume() {

        super.onResume()

        // Get screen metrics used to setup optimal bounding box image size
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)

        viewModel.setScreenProperties(metrics.widthPixels, metrics.heightPixels)

        /**
         * Before hiding the status bar, a wait is needed to let the UI settle.
         * Trying to set app to immersive mode before it's ready causes the flags not sticking.
         */
        container.postDelayed({
            container.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN and View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }, ApplicationRoot.IMMERSIVE_FLAG_TIMEOUT)

    }

    override fun subscribeToViewModel() {

        // Create the image observer which updates the UI in case of an image change
        val imageObserver = Observer<Bitmap> { newImage ->
            // Update the UI, in this case, the ImageView

            ivLoadedImage.disappearingAnimation(this)
            Glide
                .with(this)
                .load(newImage)
                .centerInside()
                .error(R.drawable.icon_photo_library)
                .placeholder(R.drawable.icon_photo_library)
                .into(ivLoadedImage)
            ivLoadedImage.appearingAnimation(this)

            extendedFabLoadAction.setOnClickListener {
                viewModel.setAlertActivityParams()
                val intent = Intent(this, NewReportActivity::class.java)
                startActivity(intent)
            }

            extendedFabLoadAction.text = getString(R.string.add_report)
            extendedFabLoadAction.icon = ContextCompat.getDrawable(this, R.drawable.icon_add_report)
            extendedFabLoadAction.iconTint = ContextCompat.getColorStateList(this, R.color.selector_ic)
            extendedFabLoadAction.backgroundTintList = ContextCompat.getColorStateList(this, R.color.selector_fab_alert_color)

        }

        // Observe the LiveData, passing in this viewLifeCycleOwner as the LifecycleOwner and the observer
        viewModel.loadedImage.observe(this, imageObserver)

    }

    override fun subscribeListeners() {

        ivLoadedImage.setOnClickListener {
            if(viewModel.loadedImage.value == null){
                loadImage()
            }
        }

        load_image_button.setOnClickListener {
            loadImage()
        }

        loaded_image_rotate_button.setOnClickListener {
            viewModel.rotateImage()
        }

    }

    override fun unsubscribe() {}

    private fun loadImage(){
        // Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"

        intent.putExtra(Intent.EXTRA_MIME_TYPES, viewModel.imageMimeTypes)
        // Launch the Intent
        startActivityForResult(intent, LoadViewModel.GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result code is RESULT_OK only if the user has selected an Image
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                LoadViewModel.GALLERY_REQUEST_CODE -> {

                    //data.getData returns the content URI for the selected Image
                    val selectedImageUri = data?.data ?: return

                    viewModel.loadImage(selectedImageUri){isSuccess ->
                        if(!isSuccess){
                            showErrorSnackBar(getString(R.string.image_load_failed))
                        }
                    }

                }
            }
        }
    }

    override fun appearingAnimations(){
        load_image_button.overshootAppearingAnimation(this)
        loaded_image_rotate_button.overshootAppearingAnimation(this)
        extendedFabLoadAction.overshootAppearingAnimation(this)
    }

}
