package com.fall.control.ui.home.first_start_dialog.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fall.control.R
import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.databinding.ItemAlertMessageBinding

class ItemsAlertMessageAdapter(
    private val selectAlertMessage: (Int) -> Unit,
    private val unselectAlertMessage: () -> Unit
) : androidx.recyclerview.widget.ListAdapter<ItemAlertMessage, ItemsAlertMessageAdapter.ViewHolder>(
    DiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ItemAlertMessage = currentList[position]
        holder.bind(item,holder.itemView.context, selectAlertMessage, unselectAlertMessage)
    }

    class ViewHolder(
        private val binding: ItemAlertMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceType")
        fun bind(
            item: ItemAlertMessage,
            context: Context,
            selectAlertMessage: (Int) -> Unit,
            unselectAlertMessage: () -> Unit
        ) {
            binding.apply {
                checkBox.isChecked = item.isSelected
                if (item.isSelected) {
                    alertMessage.setTextColor(getColor(context, R.color.caribbean_green))
                    val spannableStringBuilder = SpannableStringBuilder(item.message)
                    spannableStringBuilder.setSpan(
                        UnderlineSpan(),
                        0,
                        spannableStringBuilder.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    alertMessage.text = spannableStringBuilder
                    itemView.setOnClickListener {
                        unselectAlertMessage()
                        it.setOnClickListener(null)
                    }
                } else {
                    alertMessage.setTextColor(Color.WHITE)
                    alertMessage.text = item.message
                    itemView.setOnClickListener {
                        selectAlertMessage(item.id)
                        it.setOnClickListener(null)
                    }
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ItemAlertMessage>() {
        override fun areItemsTheSame(
            oldItem: ItemAlertMessage,
            newItem: ItemAlertMessage
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ItemAlertMessage,
            newItem: ItemAlertMessage
        ): Boolean {
            return oldItem == newItem
        }
    }
}