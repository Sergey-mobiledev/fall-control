package com.fall.control.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB : ViewBinding>() : DialogFragment(), BaseView {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseViewModel
    protected abstract val width: Int
    protected abstract val height: Int

    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    abstract fun onViewCreated()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setDimAmount(0.75f)
            }
        }
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
        _binding = getViewBinding(inflater, container)
        viewModel.attack(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(width, height)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.emitHomeFragmentAndDetach()
        Log.d("111", "BaseDialogFragment onDestroyView")
        _binding = null
    }
}