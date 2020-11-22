package com.arpadfodor.communityparking.android.app.view.utils

import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.viewmodel.utils.MasterDetailViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : AppFragment(){

    companion object{

        val TAG = DetailFragment::class.java.simpleName

        lateinit var viewModel: MasterDetailViewModel
        var title = ""

        var sendSucceedSnackBarText = ""
        var sendFailedSnackBarText = ""
        var alreadySentSnackBarText = ""
        var deletedSnackBarText = ""
        var deleteFailedSnackBarText = ""
        var updateSucceedSnackBarText = ""
        var updateFailedSnackBarText = ""

        fun setParams(
            viewModel: MasterDetailViewModel,
            title: String,
            sendSucceedSnackBarText: String,
            sendFailedSnackBarText: String,
            alreadySentSnackBarText: String,
            deletedSnackBarText: String,
            deleteFailedSnackBarText: String,
            updateSucceedSnackBarText: String,
            updateFailedSnackBarText: String
        ){
            this.viewModel = viewModel
            this.title = title

            this.sendSucceedSnackBarText = sendSucceedSnackBarText
            this.sendFailedSnackBarText = sendFailedSnackBarText
            this.alreadySentSnackBarText = alreadySentSnackBarText
            this.deletedSnackBarText = deletedSnackBarText
            this.deleteFailedSnackBarText = deleteFailedSnackBarText
            this.updateSucceedSnackBarText = updateSucceedSnackBarText
            this.updateFailedSnackBarText = updateFailedSnackBarText
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        report_detail_title?.text = title
    }

    override fun subscribeToViewModel() {

        // Create the observer
        val selectedRecognitionObserver = Observer<Int> { id ->

            val currentRecognition = viewModel.getRecognitionById(id)

            if(currentRecognition == null){
                fragment_detail_parent_layout?.visibility = View.GONE
            }

            currentRecognition?.let { report ->

                fragment_detail_parent_layout?.visibility = View.VISIBLE

                val image = report.image
                image?.let { bitmap ->

                    reportDetailImage?.let {
                        it.disappearingAnimation(requireContext())
                        Glide
                            .with(this)
                            .load(bitmap)
                            .transform(RoundedCorners(requireContext()
                                .resources.getDimension(R.dimen.image_corner_radius).toInt()))
                            .error(R.drawable.icon_image)
                            .into(it)
                        it.appearingAnimation(requireContext())
                    }

                }

                detail_send_button?.setImageResource(R.drawable.icon_send)

                //force done button on keyboard instead of the new line button
                reportDetailMessage?.setRawInputType(InputType.TYPE_CLASS_TEXT)
                reportDetailMessage?.text = SpannableStringBuilder(report.message)

                //force done button on keyboard instead of the new line button
                reportDetailPrice?.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                val feePerHourText = if(report.feePerHour == null){
                    ""
                }
                else{
                    report.feePerHour.toString()
                }
                reportDetailPrice?.text = SpannableStringBuilder(feePerHourText)

                reportDetailDate?.text = report.timestampUTC
                reportDetailLocation?.text =
                    requireContext().getString(
                        R.string.report_item_location_long,
                        report.longitude.toString(),
                        report.latitude.toString()
                    )

                when {
                    viewModel.getUserEmail() == "" -> {
                        reportReserve?.text = "Guest user cannot reserve"
                        reportReserve?.isEnabled = false
                    }
                    report.reservingEmail.isEmpty() -> {
                        reportReserve?.text = "Reserve"
                        reportReserve?.isEnabled = true
                    }
                    report.reservingEmail != viewModel.getUserEmail() -> {
                        reportReserve?.text = "Already reserved"
                        reportReserve?.isEnabled = false
                    }
                    report.reservingEmail == viewModel.getUserEmail() -> {
                        reportReserve?.text = "Delete reservation"
                        reportReserve?.isEnabled = true
                    }
                }

                viewModel.getAddressFromLocation(report.latitude, report.longitude){
                    reportDetailAddress?.text = requireContext().getString(R.string.report_item_address, it)
                }

                reportReserve?.setOnClickEvent {
                    viewModel.reserveButtonClicked(id){ isEnabled, text ->

                        val currentContext = context
                        val currentView = view
                        currentContext ?: return@reserveButtonClicked
                        currentView ?: return@reserveButtonClicked

                        reportReserve?.text = text
                        reportReserve?.isEnabled = isEnabled
                        reportReserve?.invalidate()

                    }
                }

                detail_back_button?.setOnClickListener {
                    viewModel.deselectRecognition()
                }

                detail_delete_button?.setOnClickListener {

                    viewModel.deleteReport(report.id){ isSuccess ->

                        val currentContext = context
                        val currentView = view
                        currentContext ?: return@deleteReport
                        currentView ?: return@deleteReport

                        when(isSuccess){

                            true -> {

                                AppSnackBarBuilder.buildInfoSnackBar(
                                    currentContext,
                                    currentView,
                                    deletedSnackBarText,
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            }
                            else -> {

                                AppSnackBarBuilder.buildAlertSnackBar(
                                    currentContext,
                                    currentView,
                                    deleteFailedSnackBarText,
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            }
                        }

                    }

                }

                detail_send_button?.setOnClickListener {

                    viewModel.sendReport(report.id){ isSuccess ->

                        val currentContext = context
                        val currentView = view
                        currentContext ?: return@sendReport
                        currentView ?: return@sendReport

                        when(isSuccess){
                            true -> {

                                AppSnackBarBuilder.buildSuccessSnackBar(
                                    currentContext,
                                    currentView,
                                    sendSucceedSnackBarText,
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            }
                            else -> {

                                AppSnackBarBuilder.buildAlertSnackBar(
                                    currentContext,
                                    currentView,
                                    sendFailedSnackBarText,
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            }
                        }

                    }

                }

                reportDetailMessage?.isFocusable = true
                reportDetailMessage?.isClickable = true
                reportDetailMessage?.setOnEditorActionListener { textView, actionId, event ->

                    val message = textView.text.toString()

                    return@setOnEditorActionListener when (actionId){

                        EditorInfo.IME_ACTION_DONE ->{

                            viewModel.updateRecognitionMessage(id, message){ isSuccess ->

                                val currentContext = context
                                val currentView = view
                                currentContext ?: return@updateRecognitionMessage
                                currentView ?: return@updateRecognitionMessage

                                if(!isSuccess){
                                    AppSnackBarBuilder.buildAlertSnackBar(
                                        currentContext,
                                        currentView,
                                        updateFailedSnackBarText,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }

                            }
                            reportDetailMessage?.clearFocus()
                            false

                        }
                        else -> {
                            false
                        }

                    }

                }

                reportDetailPrice?.isFocusable = true
                reportDetailPrice?.isClickable = true
                reportDetailPrice?.setOnEditorActionListener { textView, actionId, event ->

                    val priceString = textView.text.toString()
                    var price: Double? = null

                    if(priceString.isNotEmpty()) {
                        price = priceString.toDouble()
                    }

                    return@setOnEditorActionListener when (actionId){

                        EditorInfo.IME_ACTION_DONE ->{

                            viewModel.updateRecognitionPrice(id, price){ isSuccess ->

                                val currentContext = context
                                val currentView = view
                                currentContext ?: return@updateRecognitionPrice
                                currentView ?: return@updateRecognitionPrice

                                if(!isSuccess){
                                    AppSnackBarBuilder.buildAlertSnackBar(
                                        currentContext,
                                        currentView,
                                        updateFailedSnackBarText,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }

                            }
                            reportDetailPrice?.clearFocus()
                            false

                        }
                        else -> {
                            false
                        }

                    }

                }


            }

            fragment_detail_parent_layout?.invalidate()
            appearingAnimations()

        }

        // Observe the LiveData, passing in this viewLifeCycleOwner as the LifecycleOwner and the observer
        viewModel.selectedRecognitionId.observe(requireActivity(), selectedRecognitionObserver)

    }

    override fun appearingAnimations(){
        detail_send_button?.overshootAppearingAnimation(this.requireContext())
        detail_delete_button?.overshootAppearingAnimation(this.requireContext())
        detail_back_button?.overshootAppearingAnimation(this.requireContext())
    }

    override fun subscribeListeners(){}
    override fun unsubscribe(){}

}