package com.arpadfodor.communityparking.android.app.view.utils

import androidx.databinding.BindingAdapter
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.UserRecognition

@BindingAdapter("recognitionId")
fun TextView.setRecognitionId(item: UserRecognition) {
    text = item.licenseId
}

@BindingAdapter("recognitionDate")
fun TextView.setRecognitionDate(item: UserRecognition) {
    text = context.getString(R.string.recognition_item_timestamp, item.date)
}

@BindingAdapter("recognitionLocation")
fun TextView.setRecognitionLocation(item: UserRecognition) {
    text = context.getString(R.string.recognition_item_location, item.longitude.toFloat(), item.latitude.toFloat())
}

@BindingAdapter("recognitionMessage")
fun TextView.setRecognitionMessage(item: UserRecognition) {

    visibility = if(item.message.isEmpty()){
        View.GONE
    }
    else{
        View.VISIBLE
    }

    text = context.getString(R.string.recognition_item_message, item.message)

}

@BindingAdapter("recognitionEditButton")
fun ImageButton.setRecognitionEditButton(item: UserRecognition) {

    if(item.isSent){
        this.setImageResource(R.drawable.icon_info)
    }
    else{
        this.setImageResource(R.drawable.icon_edit_recognition)
    }

}

@BindingAdapter("recognitionSendButton")
fun ImageButton.setRecognitionSendButton(item: UserRecognition) {

    if(item.isAlert){
        if(item.isSent){
            this.setImageResource(R.drawable.icon_added_recognition)
        }
        else{
            this.setImageResource(R.drawable.icon_add_recognition)
        }
    }
    else{
        if(item.isSent){
            this.setImageResource(R.drawable.icon_added_recognition)
        }
        else{
            this.setImageResource(R.drawable.icon_send)
        }
    }

}

@BindingAdapter("recognitionBackground")
fun ConstraintLayout.setListElementContainer(item: UserRecognition) {

    if(item.isSelected){
        this.setBackgroundResource(R.drawable.selected_background)
    }
    else{
        this.setBackgroundResource(R.drawable.card_background)
    }

}