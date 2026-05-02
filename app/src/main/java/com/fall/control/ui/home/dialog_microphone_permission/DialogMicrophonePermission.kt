package com.fall.control.ui.home.dialog_microphone_permission

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.fall.control.R

class DialogMicrophonePermission : DialogFragment() {

    private val mode by lazy { arguments?.getString(MODE_TAG) ?: "null" }

    override fun onStart() {
        super.onStart()
        val myDialog = dialog as AlertDialog
        myDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        myDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle("Permission RECORD AUDIO denied")
            .setMessage("To find out the sound level, we need access to your microphone")
            .setPositiveButton("Ok") { _, _ ->
                when (mode) {
                    CONTROL_MODE -> {
                        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to CONTROL_MODE))
                    }

                    SOUND_LEVEL_MODE -> {
                        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to SOUND_LEVEL_MODE))
                    }

                    "null" -> {
                        setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to POSITIVE_RESULT))
                    }
                }
                dismiss()
            }
            .setNegativeButton("Cancel") { _, _ ->
                setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to NEGATIVE_RESULT))
                dismiss()
            }
            .create()
    }

    companion object {
        const val TAG = "DialogMicrophonePermission_tag"
        const val MODE_TAG = "DialogMicrophonePermission_mode_tag"
        const val CONTROL_MODE = "control_mode"
        const val SOUND_LEVEL_MODE = "sound_level_mode"
        const val POSITIVE_RESULT = "positive_result"
        const val NEGATIVE_RESULT = "negative_result"
        const val REQUEST_KEY = "DialogMicrophonePermission_request_key"
        const val BUNDLE_KEY = "DialogMicrophonePermission_bundle_key"
    }

}