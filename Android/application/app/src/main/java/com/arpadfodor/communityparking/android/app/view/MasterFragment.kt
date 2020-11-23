package com.arpadfodor.communityparking.android.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.arpadfodor.communityparking.android.app.R
import com.arpadfodor.communityparking.android.app.viewmodel.utils.MasterDetailViewModel
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report
import com.arpadfodor.communityparking.android.app.view.utils.AppFragment
import com.arpadfodor.communityparking.android.app.view.utils.AppSnackBarBuilder
import com.arpadfodor.communityparking.android.app.view.utils.ReportEventListener
import com.arpadfodor.communityparking.android.app.view.utils.ReportListAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_master.*

class MasterFragment : AppFragment(){

    companion object{

        val TAG = MasterFragment::class.java.simpleName

        lateinit var viewModel: MasterDetailViewModel
        var title = ""

        var sendSucceedSnackBarText = ""
        var sendFailedSnackBarText = ""
        var alreadySentSnackBarText = ""
        var deletedSnackBarText = ""
        var deleteFailedSnackBarText = ""

        fun setParams(viewModel: MasterDetailViewModel, title: String,
                      sendSucceedSnackBarText: String, sendFailedSnackBarText: String, alreadySentSnackBarText: String,
                      deletedSnackBarText: String, deleteFailedSnackBarText: String){

            Companion.viewModel = viewModel
            Companion.title = title

            Companion.sendSucceedSnackBarText = sendSucceedSnackBarText
            Companion.sendFailedSnackBarText = sendFailedSnackBarText
            Companion.alreadySentSnackBarText = alreadySentSnackBarText
            Companion.deletedSnackBarText = deletedSnackBarText
            Companion.deleteFailedSnackBarText = deleteFailedSnackBarText

        }

    }

    private lateinit var adapter: ReportListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_master, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        super.onViewCreated(view, savedInstanceState)
        recognition_list_title.text = title

        adapter = ReportListAdapter(requireContext(), createEventListener())
        recognition_list.adapter = adapter
        recognition_list.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun subscribeToViewModel() {

        // Create the observer
        val listObserver = Observer<List<Report>> { list ->
            adapter.submitList(list)
            adapter.notifyDataSetChanged()
        }

        // Observe the LiveData, passing in this viewLifeCycleOwner as the LifecycleOwner and the observer
        viewModel.reports.observe(requireActivity(), listObserver)

    }

    override fun appearingAnimations(){}
    override fun subscribeListeners(){}
    override fun unsubscribe(){}

    private fun createEventListener() : ReportEventListener {

        return ReportEventListener(

            editClickListener = { id ->
                viewModel.selectRecognition(id)
            },

            sendClickListener = { id ->

                val recognition = DetailFragment.viewModel.getRecognitionById(id)
                recognition?.let{

                    viewModel.sendReport(id) { isSuccess ->

                        val currentContext = context
                        val currentView = view
                        currentContext ?: return@sendReport
                        currentView ?: return@sendReport

                        when (isSuccess) {
                            true -> {
                                AppSnackBarBuilder.buildSuccessSnackBar(
                                    currentContext,
                                    currentView, sendSucceedSnackBarText,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                AppSnackBarBuilder.buildAlertSnackBar(
                                    currentContext,
                                    currentView, sendFailedSnackBarText,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }

                }

            },

            deleteClickListener = { id ->

                viewModel.deleteReport(id){ isSuccess ->

                    val currentContext = context
                    val currentView = view
                    currentContext ?: return@deleteReport
                    currentView ?: return@deleteReport

                    when (isSuccess) {
                        true -> {
                            AppSnackBarBuilder.buildInfoSnackBar(
                                currentContext,
                                currentView, deletedSnackBarText,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            AppSnackBarBuilder.buildAlertSnackBar(
                                currentContext,
                                currentView, deleteFailedSnackBarText,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                }

            }

        )

    }

}