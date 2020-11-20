package com.arpadfodor.communityparking.android.app.model.repository

import android.content.Context
import com.arpadfodor.communityparking.android.app.model.DateHandler
import com.arpadfodor.communityparking.android.app.model.db.dataclasses.DbMetaData

object GeneralRepository {

    lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
    }

    fun updateAll(callback: (isReportsSuccess: Boolean) -> Unit){
        ReportRepository.updateFromApi{ reportsSuccess ->
            callback(reportsSuccess)
        }
    }

    fun isFreshTimestamp(meta: DbMetaData, currentTimestampUTC: String) : Boolean{
        val currentDate = DateHandler.stringToDate(currentTimestampUTC)
        val dbDate = DateHandler.stringToDate(meta.modificationTimestampUTC)
        return currentDate.after(dbDate)
    }

}