package com.arpadfodor.communityparking.android.app.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.MasterDetailActivity
import com.arpadfodor.communityparking.android.app.viewmodel.ReportViewModel

class ReportActivity : MasterDetailActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(ReportViewModel::class.java)
        super.onCreate(savedInstanceState)

        listName = getString(R.string.report_list)
        detailName = getString(R.string.report_details)

        sendSucceed = getString(R.string.updated)
        sendFailed = getString(R.string.update_failed)
        deleted = getString(R.string.deleted)
        deleteFailed = getString(R.string.delete_failed)
        updateSucceed = getString(R.string.updated)
        updateFailed = getString(R.string.update_failed)

    }

}
