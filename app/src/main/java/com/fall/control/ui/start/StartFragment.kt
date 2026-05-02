package com.fall.control.ui.start

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.fall.control.R
import com.fall.control.data.repository.Repository.Companion.openChromeTabs
import com.fall.control.data.service.Animations.hideViewFadeOut
import com.fall.control.data.service.Animations.showViewScaleIn
import com.fall.control.databinding.FragmentStartBinding
import com.fall.control.ui.base.BaseFragment
import com.fall.control.ui.base.Dialog
import com.fall.control.ui.start.error_dialog.ErrorDialog
import com.fall.control.ui.start.error_dialog.ErrorDialog.Companion.BUNDLE_KEY
import com.fall.control.ui.start.error_dialog.ErrorDialog.Companion.REQUEST_KEY
import com.fall.control.ui.start.utils.StartView
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartFragment : BaseFragment<FragmentStartBinding>(), StartView {

    override val viewModel by viewModel<StartViewModel>()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentStartBinding {
        return FragmentStartBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        viewModel.getPrivacyAgree()
    }

    override fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_startFragment_to_homeFragment)
    }


    override fun updatePrivacyAgree(privacyAgree: Boolean) {
        binding.checkBox.isChecked = privacyAgree
    }

    override fun showDialog() {
        val fragmentManager = activity?.supportFragmentManager ?: return
        fragmentManager.findFragmentByTag(Dialog.TAG).let { fragment ->
            fragment ?: let {
                val dialogFragment = Dialog()
                dialogFragment.arguments =
                    bundleOf(Dialog.MESSAGE to getString(R.string.text_accept_our_terms))
                dialogFragment.show(
                    fragmentManager,
                    Dialog.TAG
                )
            }
        }
    }

    override fun showErrorDialog() {
        binding.progress.apply {
            isVisible = false
            isIndeterminate = false
        }
        val fragmentManager = activity?.supportFragmentManager ?: return
        fragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            bundle.getInt(BUNDLE_KEY)
            binding.progress.apply {
                isVisible = true
                isIndeterminate = true
            }
            viewModel.getPrivacyAgreeWithoutDelay()
        }
        fragmentManager.findFragmentByTag(ErrorDialog.TAG).let { fragment ->
            fragment ?: let {
                val dialogFragment = ErrorDialog()
                dialogFragment.show(
                    fragmentManager,
                    ErrorDialog.TAG
                )
            }
        }
    }

    override fun showLoading() {
        binding.apply {
            buttonStart.setOnClickListener(null)
            buttonOpenPrivacy.setOnClickListener(null)
//            hideViewFadeOut(checkboxLayout)
            buttonStart.isVisible = false
//            hideViewFadeOut(buttonStart)
            hideViewFadeOut(checkBox)
            hideViewFadeOut(textPrivacyPolicy)
            hideViewFadeOut(buttonOpenPrivacy)
            progress.apply {
                isVisible = true
                isIndeterminate = true
            }
        }
    }

    override fun showAllViews() {
        binding.apply {
            progress.isIndeterminate = false
            progress.isVisible = false
//            showViewScaleIn(checkboxLayout)
            showViewScaleIn(buttonStart)
            showViewScaleIn(line)
            showViewScaleIn(checkBoxBack)
            showViewScaleIn(checkBox)
            showViewScaleIn(textPrivacyPolicy)
            showViewScaleIn(buttonOpenPrivacy)
            buttonStart.setOnClickListener {
                viewModel.clickButtonStart(requireContext())
            }
            buttonOpenPrivacy.setOnClickListener {
                openChromeTabs(requireContext())
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.updatePrivacyAgree(isChecked)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("111", "onPause startFrag")
    }

    override fun onStop() {
        super.onStop()
        Log.d("111", "onStop startFrag")
    }
}