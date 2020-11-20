package com.arpadfodor.communityparking.android.app.view

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.AppActivity
import com.arpadfodor.communityparking.android.app.viewmodel.utils.AppViewModel
import com.google.android.material.navigation.NavigationView

class SettingsActivity : AppActivity() {

    override val viewModel: AppViewModel = AppViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)
        val drawer = findViewById<DrawerLayout>(R.id.settingsActivityDrawerLayout)
        val navigation = findViewById<NavigationView>(R.id.settings_navigation)
        initUi(drawer, navigation)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()

    }

    override fun appearingAnimations(){}
    override fun subscribeToViewModel(){}
    override fun subscribeListeners(){}
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
