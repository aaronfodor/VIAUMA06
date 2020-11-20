package com.arpadfodor.communityparking.android.app.view

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.AppActivity
import com.arpadfodor.communityparking.android.app.view.utils.AppSnackBarBuilder
import com.arpadfodor.communityparking.android.app.view.utils.overshootAppearingAnimation
import com.arpadfodor.communityparking.android.app.viewmodel.HowToUseViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_how_to_use.*

class HowToUseActivity : AppActivity() {

    override lateinit var viewModel: HowToUseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_how_to_use)
        val drawer = findViewById<DrawerLayout>(R.id.howToUseActivityDrawerLayout)
        val navigation = findViewById<NavigationView>(R.id.how_to_use_navigation)
        viewModel = ViewModelProvider(this).get(HowToUseViewModel::class.java)
        initUi(drawer, navigation)

    }

    override fun subscribeToViewModel(){

        // Create the Boolean observer which updates the UI in case of speaking state change
        val speakObserver = Observer<Boolean> { isSpeaking ->
            // Update the UI, in this case, the FAB
            if(isSpeaking){
                fabRead.setImageResource(R.drawable.icon_stop)
            }
            else{
                fabRead.setImageResource(R.drawable.icon_play)
            }
        }
        // Observe the LiveData, passing in this viewLifeCycleOwner as the LifecycleOwner and the observer
        viewModel.isTextToSpeechSpeaking.observe(this, speakObserver)

        viewModel.subscribeTextToSpeechListeners(
            errorCallback = {
                AppSnackBarBuilder.buildAlertSnackBar(
                    this,
                    window.decorView.rootView,
                    getString(R.string.text_to_speech_error),
                    Snackbar.LENGTH_SHORT
                )
            }
        )

    }

    override fun appearingAnimations() {
        fabRead.overshootAppearingAnimation(this)
    }

    override fun subscribeListeners(){

        fabRead.setOnClickListener {
            viewModel.textToSpeechButtonClicked(howToUseContent.text.toString())
        }

    }

    override fun unsubscribe(){}

    override fun onBackPressed() {
        if(activityDrawerLayout.isDrawerOpen(GravityCompat.START)){
            activityDrawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            this.finish()
        }
    }

}
