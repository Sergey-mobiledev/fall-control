package com.fall.control.ui.menu.info

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fall.control.databinding.FragmentInfoBinding
import com.fall.control.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class InfoFragment : BaseFragment<FragmentInfoBinding>() {

    override val viewModel by viewModel<InfoViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentInfoBinding {
        return FragmentInfoBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {}


}