package com.fall.control.ui.start.error_dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.fall.control.databinding.DialogFragmentErrorBinding

class ErrorDialog : DialogFragment(), ErrorView {

    override val width: Int = WindowManager.LayoutParams.MATCH_PARENT
    override val height: Int = WindowManager.LayoutParams.WRAP_CONTENT
    private lateinit var binding: DialogFragmentErrorBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(width, height)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
        binding = DialogFragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonReconnect.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setFragmentResult(
            REQUEST_KEY, bundleOf(
                BUNDLE_KEY to 1
            )
        )
    }

    companion object {
        const val TAG = "ErrorDialog_tag"
        const val REQUEST_KEY = "ErrorDialog_request_key"
        const val BUNDLE_KEY = "DialogMicrophonePermission_bundle_key"
    }

}