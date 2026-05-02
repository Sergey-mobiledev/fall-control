package com.fall.control.ui.menu.menu_home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fall.control.R
import com.fall.control.data.repository.Repository.Companion.openChromeTabs
import com.fall.control.databinding.FragmentMenuHomeBinding
import com.fall.control.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuHomeFragment : BaseFragment<FragmentMenuHomeBinding>() {

    override val viewModel by viewModel<MenuHomeViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMenuHomeBinding {
        return FragmentMenuHomeBinding.inflate(inflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            buttonHistory.setOnClickListener {
                findNavController().navigate(R.id.action_menuHomeFragment_to_historyFragment)
                it.setOnClickListener(null)
            }
            buttonInfo.setOnClickListener {
                findNavController().navigate(R.id.action_menuHomeFragment_to_infoFragment)
                it.setOnClickListener(null)
            }
            buttonPolicy.setOnClickListener {
                openChromeTabs(requireContext())
                it.setOnClickListener(null)
            }
            buttonSettings.setOnClickListener {
                findNavController().navigate(R.id.action_menuHomeFragment_to_settingsFragment)
                it.setOnClickListener(null)
            }
        }
    }

    override fun onViewCreated() {}

}