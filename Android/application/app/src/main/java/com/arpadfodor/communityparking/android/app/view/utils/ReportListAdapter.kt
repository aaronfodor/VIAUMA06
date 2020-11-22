package com.arpadfodor.communityparking.android.app.view.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.arpadfodor.communityparking.android.app.databinding.ReportItemBinding
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.Report

class ReportListAdapter(context: Context, clickListener: ReportEventListener) :
    ListAdapter<Report, ReportListAdapter.RecognitionViewHolder>(
        ReportDiffCallback()
    )
{

    private val context = context
    private val clickListener = clickListener

    /**
     * Nested class to hold the element's view
     **/
    class RecognitionViewHolder private constructor(binding: ReportItemBinding) : RecyclerView.ViewHolder(binding.root){

        val binding = binding

        companion object{

            fun from(parent: ViewGroup): RecognitionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReportItemBinding.inflate(layoutInflater, parent, false)
                return RecognitionViewHolder(
                    binding
                )
            }

        }

        fun bind(item: Report, clickListener: ReportEventListener?) {
            binding.reportItem = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

    override fun onBindViewHolder(holder: RecognitionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecognitionViewHolder {
        return RecognitionViewHolder.from(parent)
    }

}

class ReportDiffCallback : DiffUtil.ItemCallback<Report>() {

    override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
        return oldItem == newItem
    }

}

class ReportEventListener(
    val editClickListener: (id: Int) -> Unit,
    val sendClickListener: (id: Int) -> Unit,
    val deleteClickListener: (id: Int) -> Unit
){
    fun onEditClick(report: Report) = editClickListener(report.id)
    fun onSendClick(report: Report) = sendClickListener(report.id)
    fun onDeleteClick(report: Report) = deleteClickListener(report.id)
}