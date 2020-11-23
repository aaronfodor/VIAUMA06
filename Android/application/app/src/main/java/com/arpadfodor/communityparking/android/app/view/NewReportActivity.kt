package com.arpadfodor.communityparking.android.app.view

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.AppActivity
import com.arpadfodor.communityparking.android.app.viewmodel.NewReportViewModel
import com.google.android.material.navigation.NavigationView

class NewReportActivity : AppActivity() {

    override lateinit var viewModel: NewReportViewModel
    private lateinit var container: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_report)
        container = findViewById(R.id.new_report_container)
        val drawer = findViewById<DrawerLayout>(R.id.newReportActivityDrawerLayout)
        val navigation = findViewById<NavigationView>(R.id.new_report_navigation)
        viewModel = ViewModelProvider(this).get(NewReportViewModel::class.java)
        initUi(drawer, navigation)

    }

    override fun onResume() {
        super.onResume()
        // Prepare the fragment to listen to the appropriate ViewModel
        NewReportFragment.setParams(viewModel)
    }

    override fun subscribeToViewModel() {
        showNewReportFragment()
    }

    override fun onBackPressed() {
        if(activityDrawerLayout.isDrawerOpen(GravityCompat.START)){
            activityDrawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            this.finish()
        }
    }

    override fun appearingAnimations(){}
    override fun subscribeListeners(){}
    override fun unsubscribe(){}

    private fun showNewReportFragment(){

        val newReportFragment = NewReportFragment()

        newReportFragment.let {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim)
                .replace(R.id.new_report_container, it)
                .addToBackStack(null)
                .commit()
        }

    }

}
