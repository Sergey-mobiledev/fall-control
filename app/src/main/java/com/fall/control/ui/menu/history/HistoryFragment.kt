package com.fall.control.ui.menu.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import com.fall.control.R
import com.fall.control.data.model.ItemFallHistory
import com.fall.control.databinding.FragmentHistoryBinding
import com.fall.control.ui.base.BaseFragment
import com.fall.control.ui.menu.history.utils.HistoryView
import com.fall.control.ui.menu.history.utils.ItemFallHistoryAdapter
import com.fall.control.ui.menu.history_1.HistoryOneFragment.Companion.SOME_ITEM_FALL_HISTORY_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(), HistoryView {

    override val viewModel by viewModel<HistoryViewModel>()
    private val itemFallHistoryAdapter = ItemFallHistoryAdapter(openSomeItemFall = {
        findNavController().navigate(
            R.id.action_historyFragment_to_historyOneFragment,
            bundleOf(SOME_ITEM_FALL_HISTORY_ID to it)
        )
    })

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHistoryBinding {
        return FragmentHistoryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        binding.apply {
            itemsFallHistoryList.apply {
                isMotionEventSplittingEnabled = false
                adapter = itemFallHistoryAdapter
                val itemAnimator = itemAnimator
                if (itemAnimator is DefaultItemAnimator)
                    itemAnimator.supportsChangeAnimations = false
            }
        }
        viewModel.getFallHistory()
    }

    override fun setData(fallHistory: List<ItemFallHistory>) {
        binding.textEmptyList.isVisible = fallHistory.isEmpty()
        itemFallHistoryAdapter.submitList(fallHistory)
    }


}