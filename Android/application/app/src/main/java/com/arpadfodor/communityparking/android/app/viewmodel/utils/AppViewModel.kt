package com.arpadfodor.communityparking.android.app.viewmodel.utils

import androidx.lifecycle.ViewModel
import com.arpadfodor.communityparking.android.app.model.AccountService

open class AppViewModel : ViewModel(){

    fun getCurrentUserName() : String {
        return AccountService.getDisplayUserName()
    }

    fun getCurrentUserEmail() : String {
        return AccountService.getDisplayUserEmail()
    }

}