package com.fall.control.ui.menu.history_1

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import com.fall.control.R
import com.fall.control.data.model.ItemFallHistory
import com.fall.control.databinding.FragmentHistoryOneBinding
import com.fall.control.ui.base.BaseFragment
import com.fall.control.ui.menu.history_1.utils.HistoryOneView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat

class HistoryOneFragment : BaseFragment<FragmentHistoryOneBinding>(), HistoryOneView {

    private val someItemFallHistoryId by lazy { arguments?.getInt(SOME_ITEM_FALL_HISTORY_ID) ?: 0 }
    override val viewModel by viewModel<HistoryOneViewModel>(parameters = {
        parametersOf(
            someItemFallHistoryId
        )
    })

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryOneBinding {
        return FragmentHistoryOneBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        viewModel.getSomeItemFall()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun setSomeItemFall(itemFallHistory: ItemFallHistory) {
        binding.apply {
            if (itemFallHistory.soundLevelValue > 100) {
                valueVolumeMeter.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            }
            if (itemFallHistory.xWidth > 25 || itemFallHistory.xWidth < -25) {
                textSmallX.setTextColor(getColor(requireContext(), R.color.saturated_pink))
                valueX.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            }
            if (itemFallHistory.yWidth > 25 || itemFallHistory.yWidth < -25) {
                textSmallY.setTextColor(getColor(requireContext(), R.color.saturated_pink))
                valueY.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            }
            if (itemFallHistory.zWidth > 25 || itemFallHistory.zWidth < -25) {
                textSmallZ.setTextColor(getColor(requireContext(), R.color.saturated_pink))
                valueZ.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            }
            valueVolumeMeter.text = itemFallHistory.soundLevelValue.toString()
            titleHistory.text = SimpleDateFormat("dd.MM.yyyy").format(itemFallHistory.startTime)
            startTimeValue.text = SimpleDateFormat("HH:mm").format(itemFallHistory.startTime)
            endTimeValue.text = SimpleDateFormat("HH:mm").format(itemFallHistory.endTime)
            valueX.text = "=" + itemFallHistory.xWidth
            valueY.text = "=" + itemFallHistory.yWidth
            valueZ.text = "=" + itemFallHistory.zWidth
            if (itemFallHistory.isMessageSent) {
                textIsSendMessage.text = getString(R.string.the_message_was_send)
            } else {
                textIsSendMessage.text = getString(R.string.the_message_was_not_send)
            }
        }
    }

    companion object {
        const val SOME_ITEM_FALL_HISTORY_ID = "SOME_ITEM_FALL_HISTORY_ID"
    }


}