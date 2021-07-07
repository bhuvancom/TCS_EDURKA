package com.tcs.edureka.ui.activity.call

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tcs.edureka.R
import com.tcs.edureka.databinding.CallItemBinding
import com.tcs.edureka.model.CallLogModel

/**
 * @author Bhuvaneshvar
 */
class CallAdapter(val onTap: (Long) -> Unit) : RecyclerView.Adapter<CallAdapter.CallViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<CallLogModel>() {
        override fun areItemsTheSame(oldItem: CallLogModel, newItem: CallLogModel): Boolean =
                oldItem == newItem


        override fun areContentsTheSame(oldItem: CallLogModel, newItem: CallLogModel): Boolean =
                oldItem.number == newItem.number && oldItem.time == oldItem.time

    }

    val differ = AsyncListDiffer(this, diffUtil)

    class CallViewHolder(private val callItemBinding: CallItemBinding) : RecyclerView.ViewHolder(callItemBinding.root) {
        fun bind(callLogModel: CallLogModel) {
            callItemBinding.date.text = callLogModel.time
            callItemBinding.mobile.text = callLogModel.number.toString()
            callItemBinding.name.text = callLogModel.name
            callItemBinding.type.text = callLogModel.type
            if ("MISSED".equals(callLogModel.type, true)) {
                callItemBinding.type.setTextColor(Color.RED)
            } else {
                callItemBinding.type.setTextColor(Color.GREEN)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val inflate = LayoutInflater.from(parent.context)
                .inflate(R.layout.call_item, parent, false)
        return CallViewHolder(CallItemBinding.bind(inflate))
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onTap(item.number)
        }

    }

    override fun getItemCount(): Int = differ.currentList.size
}