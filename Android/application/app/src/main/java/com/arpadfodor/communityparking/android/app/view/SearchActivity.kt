package com.arpadfodor.communityparking.android.app.view

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.AppActivity
import com.arpadfodor.communityparking.android.app.view.utils.overshootAppearingAnimation
import com.arpadfodor.communityparking.android.app.viewmodel.SearchViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_search.*

class SearchActivity : AppActivity() {

    override val viewModel: SearchViewModel = SearchViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)
        val drawer = findViewById<DrawerLayout>(R.id.searchActivityDrawerLayout)
        val navigation = findViewById<NavigationView>(R.id.search_navigation)
        initUi(drawer, navigation)

        btnUseMyLocation.setOnClickListener {
            viewModel.getClosestReportToDeviceLocation() { reportId ->
                showReport(reportId)
            }
        }

        fabSearch.setOnClickListener {
            viewModel.getClosestReportIdToAddress(etSearchByAddress.text.toString()) { reportId ->
                showReport(reportId)
            }
        }

    }

    private fun showReport(id: Int){
        val intent = Intent(this, ReportActivity::class.java)
        intent.putExtra(getString(R.string.INTENT_EXTRA_SELECTED_ID_KEY), id)
        startActivity(intent)
    }

    override fun appearingAnimations() {
        fabSearch.overshootAppearingAnimation(this)
    }

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
