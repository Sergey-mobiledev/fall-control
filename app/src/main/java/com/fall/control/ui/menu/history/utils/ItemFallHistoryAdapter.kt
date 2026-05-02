package com.fall.control.ui.menu.history.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fall.control.R
import com.fall.control.data.model.ItemFallHistory
import com.fall.control.databinding.ItemFallHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ItemFallHistoryAdapter(
    private val openSomeItemFall: (Int) -> Unit
) : androidx.recyclerview.widget.ListAdapter<ItemFallHistory, ItemFallHistoryAdapter.ViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFallHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ItemFallHistory = currentList[position]
        holder.bind(item, holder.itemView.context, openSomeItemFall)
    }

    class ViewHolder(
        private val binding: ItemFallHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: ItemFallHistory,
            context: Context,
            openSomeItemFall: (Int) -> Unit
        ) {
            binding.apply {
                if (item.isFall) {
                    if (item.id % 2 == 0){
                        rootItem.setBackgroundResource(R.drawable.selector_back_item_fall_even)
                    } else {
                        rootItem.setBackgroundResource(R.drawable.selector_back_item_fall_even_not)
                    }
                    iconEye.setImageResource(R.drawable.ic_eye_pink)
                    endTimeValue.setTextColor(getColor(context, R.color.saturated_pink))
                    textEnd.setTextColor(getColor(context, R.color.saturated_pink))
                    textEnd.text = getString(context, R.string.fall)
                    rootItem.setOnClickListener {
                        openSomeItemFall(item.id)
                    }
                } else {
                    if (item.id % 2 == 0){
                        rootItem.setBackgroundResource(R.drawable.back_item_fall_transparent)
                    } else {
                        rootItem.setBackgroundResource(R.drawable.back_item_fall)
                    }
                }
                textCurrentDate.text = DATE_FORMAT.format(item.startTime)
                startTimeValue.text = TIME_FORMAT.format(item.startTime)
                endTimeValue.text = TIME_FORMAT.format(item.endTime)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ItemFallHistory>() {
        private val DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        private val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.getDefault())

        override fun areItemsTheSame(
            oldItem: ItemFallHistory,
            newItem: ItemFallHistory
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ItemFallHistory,
            newItem: ItemFallHistory
        ): Boolean {
            return oldItem == newItem
        }
    }
}