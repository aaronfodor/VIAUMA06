package com.arpadfodor.communityparking.android.app.view

import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.view.utils.*
import com.arpadfodor.communityparking.android.app.viewmodel.NewReportViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_new_report.*
import kotlinx.android.synthetic.main.fragment_new_report.detail_send_button
import kotlinx.android.synthetic.main.fragment_new_report.fragment_detail_parent_layout
import kotlinx.android.synthetic.main.fragment_new_report.reportDetailAddress
import kotlinx.android.synthetic.main.fragment_new_report.reportDetailDate
import kotlinx.android.synthetic.main.fragment_new_report.reportDetailImage
import kotlinx.android.synthetic.main.fragment_new_report.reportDetailLocation
import kotlinx.android.synthetic.main.fragment_new_report.reportDetailMessage
import kotlinx.android.synthetic.main.fragment_new_report.reportDetailPrice
import kotlinx.android.synthetic.main.fragment_new_report.reportReserve
import kotlinx.android.synthetic.main.fragment_new_report.report_detail_title

class NewReportFragment : AppFragment(){

    companion object{

        val TAG = NewReportFragment::class.java.simpleName
        lateinit var viewModel: NewReportViewModel

        fun setParams(viewModel: NewReportViewModel){
            Companion.viewModel = viewModel
        }

    }

    private var title = ""
    private var sendSucceed = ""
    private var sendFailed = ""
    private var updateSucceed = ""
    private var updateFailed = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = getString(R.string.new_report_details)
        sendSucceed = getString(R.string.new_report_sent)
        sendFailed = getString(R.string.new_report_sending_failed)
        updateSucceed = getString(R.string.updated)
        updateFailed = getString(R.string.update_failed)

        report_detail_title?.text = title
    }

    override fun subscribeToViewModel() {

        viewModel.report.let { report ->

            fragment_detail_parent_layout?.visibility = View.VISIBLE

            viewModel.image.let { bitmap ->

                reportDetailImage?.let {
                    it.disappearingAnimation(requireContext())
                    Glide
                        .with(this)
                        .load(bitmap)
                        .error(R.drawable.icon_image)
                        .into(it)
                    it.appearingAnimation(requireContext())
                }

            }

            detail_send_button?.setImageResource(R.drawable.icon_add_report)

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
                report.reservedByEmail.isEmpty() -> {
                    reportReserve?.text = "Reserve"
                    reportReserve?.isEnabled = true
                }
                report.reservedByEmail != viewModel.getUserEmail() -> {
                    reportReserve?.text = "Already reserved"
                    reportReserve?.isEnabled = false
                }
                report.reservedByEmail == viewModel.getUserEmail() -> {
                    reportReserve?.text = "Delete reservation"
                    reportReserve?.isEnabled = true
                }
            }

            viewModel.getAddressFromLocation(report.latitude, report.longitude){
                reportDetailAddress?.text = requireContext().getString(R.string.report_item_address, it)
            }

            reportReserve?.setOnClickEvent {
                viewModel.reserveButtonClicked(){ isEnabled, text ->

                    val currentContext = context
                    val currentView = view
                    currentContext ?: return@reserveButtonClicked
                    currentView ?: return@reserveButtonClicked

                    reportReserve?.text = text
                    reportReserve?.isEnabled = isEnabled
                    reportReserve?.invalidate()

                }
            }

            detail_send_button?.setOnClickListener {

                viewModel.sendReport(){ isSuccess ->

                    val currentContext = context
                    val currentView = view
                    currentContext ?: return@sendReport
                    currentView ?: return@sendReport

                    when(isSuccess){
                        true -> {

                            AppSnackBarBuilder.buildSuccessSnackBar(
                                currentContext,
                                currentView,
                                sendSucceed,
                                Snackbar.LENGTH_SHORT
                            ).show()

                        }
                        else -> {

                            AppSnackBarBuilder.buildAlertSnackBar(
                                currentContext,
                                currentView,
                                sendFailed,
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

                        viewModel.updateRecognitionMessage(message){ isSuccess ->

                            val currentContext = context
                            val currentView = view
                            currentContext ?: return@updateRecognitionMessage
                            currentView ?: return@updateRecognitionMessage

                            if(!isSuccess){
                                AppSnackBarBuilder.buildAlertSnackBar(
                                    currentContext,
                                    currentView,
                                    updateSucceed,
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

                        viewModel.updateRecognitionPrice(price){ isSuccess ->

                            val currentContext = context
                            val currentView = view
                            currentContext ?: return@updateRecognitionPrice
                            currentView ?: return@updateRecognitionPrice

                            if(!isSuccess){
                                AppSnackBarBuilder.buildAlertSnackBar(
                                    currentContext,
                                    currentView,
                                    updateFailed,
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

        appearingAnimations()

        }

    override fun appearingAnimations() {
        reportDetailImage?.overshootAppearingAnimation(this.requireContext())
        reportDetailMessage?.overshootAppearingAnimation(this.requireContext())
        detail_send_button?.overshootAppearingAnimation(this.requireContext())
    }
    override fun subscribeListeners() {}
    override fun unsubscribe() {}

}