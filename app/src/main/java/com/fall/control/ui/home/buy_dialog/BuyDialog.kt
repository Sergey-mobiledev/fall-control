package com.fall.control.ui.home.buy_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.fall.control.data.service.Animations.hideViewFadeOut
import com.fall.control.data.service.Animations.showViewScaleIn
import com.fall.control.databinding.DialogFragmentBuyBinding
import com.fall.control.ui.base.BaseDialogFragment
import com.fall.control.ui.home.buy_dialog.utils.BuyView
import org.koin.androidx.viewmodel.ext.android.viewModel

class BuyDialog : BaseDialogFragment<DialogFragmentBuyBinding>(), BuyView {

    override val viewModel by viewModel<BuyViewModel>()
    override val width = WindowManager.LayoutParams.MATCH_PARENT
    override val height = WindowManager.LayoutParams.MATCH_PARENT

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFragmentBuyBinding {
        return DialogFragmentBuyBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        binding.apply {
            buttonBack.setOnClickListener {
                dismiss()
                it.setOnClickListener(null)
            }
            buttonRoot.setOnClickListener {
                dismiss()
                it.setOnClickListener(null)
            }

            buttonBuy.setOnClickListener {
                buttonBack.setOnClickListener(null)
                buttonRoot.setOnClickListener(null)
                viewModel.apply {
                    subscribeBilling(requireActivity())
                    initInAppPurchases()
                    hideViewFadeOut(layout)
                    progress.apply {
                        showViewScaleIn(this)
                        isIndeterminate = true
                    }
                }
            }
            viewModel.updateIsOpen()
        }
    }

    override fun dismiss() {
        super.dismiss()
        dialog?.dismiss()
    }


}