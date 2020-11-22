package com.arpadfodor.communityparking.android.app.view.utils

import androidx.databinding.BindingAdapter
import android.widget.TextView
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report

@BindingAdapter("ReportPrice")
fun TextView.setReportPrice(item: Report) {
    text = if (item.feePerHour == null) {
        context.getString(R.string.report_item_price_unknown)
    }
    else{
        context.getString(R.string.report_item_price_long, item.feePerHour.toString())
    }
}

@BindingAdapter("ReportDate")
fun TextView.setReportDate(item: Report) {
    text = context.getString(R.string.report_item_timestamp, item.timestampUTC)
}

@BindingAdapter("ReportLocation")
fun TextView.setReportLocation(item: Report) {
    text = context.getString(R.string.report_item_location, item.longitude.toFloat(), item.latitude.toFloat())
}

@BindingAdapter("ReportReservation")
fun TextView.setReportReservation(item: Report) {

    val reservedText = if(item.reservingEmail.isNotEmpty()){
        context.getString(R.string.report_item_reserved)
    }
    else{
        context.getString(R.string.report_item_not_reserved)
    }

    text = reservedText
}