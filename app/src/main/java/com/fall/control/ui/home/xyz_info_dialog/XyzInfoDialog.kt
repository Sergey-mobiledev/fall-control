package com.fall.control.ui.home.xyz_info_dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import com.fall.control.databinding.DialogFragmentXyzInfoBinding
import com.fall.control.ui.base.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class XyzInfoDialog : BaseDialogFragment<DialogFragmentXyzInfoBinding>() {

    override val viewModel by viewModel<XyzInfoViewModel>()
    override val width = WindowManager.LayoutParams.MATCH_PARENT
    override val height = WindowManager.LayoutParams.MATCH_PARENT

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFragmentXyzInfoBinding {
        return DialogFragmentXyzInfoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        binding.apply {
            buttonRoot.setOnClickListener {
                dismiss()
            }
            buttonBack.setOnClickListener {
                dismiss()
            }
        }
    }
}