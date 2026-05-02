package com.fall.control.ui.home.timer_dialog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.fall.control.databinding.DialogFragmentTimerBinding
import com.fall.control.ui.base.BaseDialogFragment
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.BUNDLE_KEY
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.NEGATIVE_RESULT
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.POSITIVE_RESULT
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.REQUEST_KEY
import com.fall.control.ui.home.timer_dialog.utils.TimerView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class TimerDialog : BaseDialogFragment<DialogFragmentTimerBinding>(), TimerView {

    override val viewModel by viewModel<TimerDialogViewModel>()
    override val width = WindowManager.LayoutParams.MATCH_PARENT
    override val height = WindowManager.LayoutParams.MATCH_PARENT
    private var snackBar: Snackbar? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFragmentTimerBinding {
        return DialogFragmentTimerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        binding.apply {
            hoursPicker.apply {
                minValue = 0
                maxValue = 23
                value = 0
            }
            minutesPicker.apply {
                minValue = 0
                maxValue = 59
                value = 30
            }
            buttonRoot.setOnClickListener {
                dismiss()
            }
            buttonBack.setOnClickListener {
                dismiss()
            }
            buttonSet.setOnClickListener {
                setTimer()
            }
        }
    }

    private fun setTimer() {
        binding.apply {
            val timerValueInMillis =
                ((hoursPicker.value * 60 + minutesPicker.value) * 60 * 1000).toLong()
            viewModel.setTimer(timerValueInMillis)
        }
    }

    private fun checkPermissionRecordAudio() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("111", "checkPermission: permission is granted")
            setTimer()
        } else if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Log.d("111", "requestPermission: permission is denied")
            showDialogMicrophonePermission()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("111", "PERMISSION GRANTED")
            setTimer()
        } else {
            Log.d("111", "PERMISSION NOT GRANTED")
            showSnack()
        }
    }

    private fun showDialogMicrophonePermission() {
        val fragmentManager = activity?.supportFragmentManager ?: return
        fragmentManager.findFragmentByTag(DialogMicrophonePermission.TAG).let { fragment ->
            fragment ?: let {
                val dialogFragment = DialogMicrophonePermission()
                dialogFragment.show(
                    fragmentManager,
                    DialogMicrophonePermission.TAG
                )
            }
        }
        fragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY)
            when (result) {
                POSITIVE_RESULT -> {
                    requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }

                NEGATIVE_RESULT -> {
                    showSnack()
                }
            }
        }
    }

    private fun showSnack() {
        snackBar = Snackbar.make(
            requireView(),
            ("Open Permissions and grant the RECORD AUDIO permission"),
            Snackbar.LENGTH_INDEFINITE
        )
            .setTextColor(Color.BLACK)
            .setBackgroundTint(Color.WHITE)
            .setAction("Open settings") {
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + requireActivity().packageName)
                )
                startActivityForResult(appSettingsIntent, 123)
            }
            .setActionTextColor(Color.BLACK)
        snackBar!!.show()
        binding.snackBarBack.apply {
            isVisible = true
            setOnClickListener {
                snackBar?.dismiss()
                isVisible = false
                it.setOnClickListener(null)
            }
        }
    }

    override fun clearTimerDialog() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}