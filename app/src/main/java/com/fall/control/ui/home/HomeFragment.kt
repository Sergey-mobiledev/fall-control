package com.fall.control.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.fall.control.R
import com.fall.control.data.model.UserSettings
import com.fall.control.data.repository.AccelerometerRepository
import com.fall.control.data.service.FallDetectionService
import com.fall.control.databinding.FragmentHomeBinding
import com.fall.control.ui.base.BaseFragment
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.BUNDLE_KEY
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.CONTROL_MODE
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.MODE_TAG
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.NEGATIVE_RESULT
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.REQUEST_KEY
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.SOUND_LEVEL_MODE
import com.fall.control.ui.home.utils.HomeView
import com.fall.control.ui.menu.MenuFragment
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeView {

    override val viewModel by viewModel<HomeViewModel>()
    private var snackBar: Snackbar? = null
    private var pendingControlModeAction: (() -> Unit)? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        registerMicrophonePermissionDialogResult()
        viewModel.subscribeUserSettings()
        viewModel.getIsTextMessageEmpty()
        binding.apply {
            buttonStartStopControl.setOnClickListener {
                ensureNotificationPermissionForControlMode {
                    checkPermissionRecordAudio(CONTROL_MODE) {
                        viewModel.updateIsActiveControlMode()
                    }
                }
            }
            buttonSoundLevelMeter.setOnClickListener {
                if (buttonSoundLevelMeter.isSelected) {
                    viewModel.updateIsSoundLevelMeterOn()
                } else {
                    checkPermissionRecordAudio(SOUND_LEVEL_MODE) { viewModel.updateIsSoundLevelMeterOn() }
                }
            }
            buttonAccelerometer.setOnClickListener {
                viewModel.updateIsAccelerometerOn()
            }
            buttonMenu.setOnClickListener {
                openMenuFragment()
            }
            buttonTimer.setOnClickListener {
                openTimerDialog()
            }
            graphLayout.setOnClickListener {
                openXyzInfoDialog()
            }

        }
    }

    override fun updateUserSettings(userSettings: UserSettings) {
        if (userSettings.isActiveControlMode) {
            FallDetectionService.startOrSync(requireContext().applicationContext)
        } else {
            FallDetectionService.stop(requireContext().applicationContext)
        }
        binding.apply {
            userSettings.apply {
                if (isActiveControlMode) setControlModeActive() else setControlModeDisabled()

                if (isAccelerometerOn) setAccelerometerOn() else setAccelerometerOff()
                when (isOpen) {
                    1 -> {
                        openBuyDialog()
                    }

                    2 -> {
                        icBuyPossibility.isVisible = false
                        icKey.isVisible = false
                        valueVolumeMeter.isVisible = true
                    }

                    3 -> {
                        icBuyPossibility.isVisible = true
                        icKey.isVisible = true
                        valueVolumeMeter.isVisible = false
                    }
                }
                if (isSoundLevelMeterOn) setSoundLevelMeterOn() else setSoundLevelMeterOff()
            }


            if (userSettings.startTimerTime != 0.toLong()) {
                timerValue.isVisible = true
                buttonTimer.setImageResource(R.drawable.selector_ic_button_timer_selected)
            } else {
                buttonTimer.setImageResource(R.drawable.selector_ic_button_timer)
                timerValue.isVisible = false
            }
        }
    }

    override fun openMenuFragment() {
        binding.fullScreenContainerView.isVisible = true
        val currentFragmentId = viewModel.getCurrentFragmentId()
        if (currentFragmentId == R.id.menuHomeFragment) return
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            ?.addToBackStack(null)
            ?.add(R.id.full_screen_container_view, MenuFragment())
            ?.commit()
    }

    override fun openFirstStartDialog() {
        if (findNavController().currentDestination?.id != R.id.firstStartDialog)
            findNavController().navigate(R.id.firstStartDialog)
    }

    override fun openTimerDialog() {
        if (findNavController().currentDestination?.id != R.id.timerDialog)
            findNavController().navigate(R.id.timerDialog)
    }

    override fun openXyzInfoDialog() {
        if (findNavController().currentDestination?.id != R.id.xyzInfoDialog)
            findNavController().navigate(R.id.xyzInfoDialog)
    }

    override fun openBuyDialog() {
        binding.apply {
            icBuyPossibility.isVisible = true
            icKey.isVisible = true
            valueVolumeMeter.isVisible = false
        }
        if (findNavController().currentDestination?.id != R.id.buyDialog)
            findNavController().navigate(R.id.buyDialog)
    }

    private fun setSoundLevelMeterOn() {
        binding.apply {
            icBuyPossibility.isVisible = false
            valueVolumeMeter.isVisible = true
            buttonSoundLevelMeter.isSelected = true
        }
        viewModel.subscribeSoundLevelMeter()

    }

    private fun setSoundLevelMeterOff() {
        viewModel.stopSoundLevelMeter()
        binding.apply {
            valueVolumeMeter.text = "0"
            buttonSoundLevelMeter.isSelected = false
        }
    }

    private fun setAccelerometerOn() {
        viewModel.subscribeAccelerometer()
        binding.apply {
            buttonAccelerometer.isSelected = true
            if (backActiveControlMode.isVisible) {
                textSmallX.setTextColor(Color.WHITE)
                textBigX.setTextColor(Color.WHITE)
                textSmallY.setTextColor(Color.WHITE)
                textBigY.setTextColor(Color.WHITE)
                textSmallZ.setTextColor(Color.WHITE)
                textBigZ.setTextColor(Color.WHITE)
                xImagePlus.setBackgroundColor(getColor(requireContext(), R.color.green_spring))
                xImageMinus.setBackgroundColor(getColor(requireContext(), R.color.green_spring))
                yImagePlus.setBackgroundColor(getColor(requireContext(), R.color.green_spring))
                yImageMinus.setBackgroundColor(getColor(requireContext(), R.color.green_spring))
                zImagePlus.setBackgroundColor(getColor(requireContext(), R.color.green_spring))
                zImageMinus.setBackgroundColor(getColor(requireContext(), R.color.green_spring))
            } else {
                textSmallX.setTextColor(getColor(requireContext(), R.color.dark_orange))
                textBigX.setTextColor(getColor(requireContext(), R.color.dark_orange))
                xImagePlus.setBackgroundColor(getColor(requireContext(), R.color.dark_orange))
                xImageMinus.setBackgroundColor(getColor(requireContext(), R.color.dark_orange))
                textSmallY.setTextColor(getColor(requireContext(), R.color.caribbean_green))
                textBigY.setTextColor(getColor(requireContext(), R.color.caribbean_green))
                yImagePlus.setBackgroundColor(getColor(requireContext(), R.color.caribbean_green))
                yImageMinus.setBackgroundColor(getColor(requireContext(), R.color.caribbean_green))
                textSmallZ.setTextColor(getColor(requireContext(), R.color.blue))
                textBigZ.setTextColor(getColor(requireContext(), R.color.blue))
                zImagePlus.setBackgroundColor(getColor(requireContext(), R.color.blue))
                zImageMinus.setBackgroundColor(getColor(requireContext(), R.color.blue))
            }

        }
    }

    private fun setAccelerometerOff() {
        viewModel.stopAccelerometer()
        binding.apply {
            buttonAccelerometer.isSelected = false
            textSmallX.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            textSmallY.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            textSmallZ.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            textBigX.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            textBigY.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            textBigZ.setTextColor(getColor(requireContext(), R.color.saturated_pink))
            xImagePlus.isVisible = false
            xImageMinus.isVisible = false
            yImagePlus.isVisible = false
            yImageMinus.isVisible = false
            zImagePlus.isVisible = false
            zImageMinus.isVisible = false
        }
    }

    private fun setControlModeActive() {
        binding.apply {
            backActiveControlMode.isVisible = true
            buttonControlMode.setBackgroundResource(R.drawable.selector_ic_control_mode_active)
            iconButtonStartStopControl.setImageResource(R.drawable.selector_ic_stop_control_mode)
            textButtonStartStopControl.text = getString(R.string.stop_control)
            imageVolumeMeter.setImageResource(R.drawable.ic_volume_meter_active)
        }
    }

    private fun setControlModeDisabled() {
        binding.apply {
            backActiveControlMode.isVisible = false
            imageVolumeMeter.setImageResource(R.drawable.ic_volume_meter)
            iconButtonStartStopControl.setImageResource(R.drawable.selector_ic_start_control_mode)
            textButtonStartStopControl.text = getString(R.string.start_control)
            buttonControlMode.setBackgroundResource(
                R.drawable.selector_ic_control_mode_disabled
            )
        }
    }

    override fun setSoundLevelValue(soundLevelValue: Int) {
        binding.valueVolumeMeter.text = if (soundLevelValue > 0) soundLevelValue.toString() else "0"
    }

    override fun setTimerValue(timerValue: Long) {
        binding.apply {
            this.timerValue.isVisible = true
            this.timerValue.text = formatDuration(timerValue)
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun setAccelerometerValue(dataAccelerometer: List<AccelerometerRepository.SensorValue>) {
        binding.apply {
            xImageMinus.isVisible = false
            xImagePlus.isVisible = false
            yImageMinus.isVisible = false
            yImagePlus.isVisible = false
            zImageMinus.isVisible = false
            zImagePlus.isVisible = false
            val currentWidth = xLayout.width / 40
            val xWidth = (currentWidth * dataAccelerometer[0].value).toInt()
            valueX.text = "=${dataAccelerometer[0].value}${getString(R.string.m_s)}"
            if (dataAccelerometer[0].aboveZero) {
                val layoutParams = xImagePlus.layoutParams
                layoutParams.width = xWidth
                xImagePlus.layoutParams = layoutParams
                xImagePlus.isVisible = true
            } else {
                val layoutParams = xImageMinus.layoutParams
                layoutParams.width = xWidth * -1
                xImageMinus.layoutParams = layoutParams
                xImageMinus.isVisible = true
            }
            val yWidth = (currentWidth * dataAccelerometer[1].value).toInt()
            valueY.text = "=${dataAccelerometer[1].value}${getString(R.string.m_s)}"
            if (dataAccelerometer[1].aboveZero) {
                val layoutParams = yImagePlus.layoutParams
                layoutParams.width = yWidth
                yImagePlus.layoutParams = layoutParams
                yImagePlus.isVisible = true
            } else {
                val layoutParams = yImageMinus.layoutParams
                layoutParams.width = yWidth * -1
                yImageMinus.layoutParams = layoutParams
                yImageMinus.isVisible = true
            }
            val zWidth = (currentWidth * dataAccelerometer[2].value).toInt()
            valueZ.text = "=${dataAccelerometer[2].value}${getString(R.string.m_s)}"
            if (dataAccelerometer[2].aboveZero) {
                val layoutParams = zImagePlus.layoutParams
                layoutParams.width = zWidth
                zImagePlus.layoutParams = layoutParams
                zImagePlus.isVisible = true
            } else {
                val layoutParams = zImageMinus.layoutParams
                layoutParams.width = zWidth * -1
                zImageMinus.layoutParams = layoutParams
                zImageMinus.isVisible = true
            }
        }
    }

    private fun checkPermissionRecordAudio(mode: String, function1: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            function1()
        } else if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_DENIED
        ) {
            showDialogMicrophonePermission(mode)
        }
    }

    private val requestPermissionLauncherControlMode = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.updateIsActiveControlMode()
        } else {
            showSnack()
        }
    }

    private val requestPermissionLauncherSoundLevelMode = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.updateIsSoundLevelMeterOn()
        } else {
            showSnack()
        }
    }

    private fun registerMicrophonePermissionDialogResult() {
        val fragmentManager = activity?.supportFragmentManager ?: return
        fragmentManager.setFragmentResultListener(REQUEST_KEY, this) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY)
            when (result) {
                CONTROL_MODE -> {
                    requestPermissionLauncherControlMode.launch(Manifest.permission.RECORD_AUDIO)
                }

                SOUND_LEVEL_MODE -> {
                    requestPermissionLauncherSoundLevelMode.launch(Manifest.permission.RECORD_AUDIO)
                }

                NEGATIVE_RESULT -> {
                    showSnack()
                }
            }
        }
    }

    private fun showDialogMicrophonePermission(mode: String) {
        val fragmentManager = activity?.supportFragmentManager ?: return
        fragmentManager.findFragmentByTag(DialogMicrophonePermission.TAG).let { fragment ->
            fragment ?: let {
                val dialogFragment = DialogMicrophonePermission()
                dialogFragment.arguments = bundleOf(MODE_TAG to mode)
                dialogFragment.show(
                    fragmentManager,
                    DialogMicrophonePermission.TAG
                )
            }
        }
    }

    private fun showSnack() {
        showPermissionSnack(
            "Open Permissions and grant the RECORD AUDIO permission"
        )
    }

    private fun showPermissionSnack(message: String) {
        snackBar = Snackbar.make(
            requireView(),
            message,
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

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            showNotificationPermissionDialog()
            pendingControlModeAction = null
            return@registerForActivityResult
        }
        pendingControlModeAction?.invoke()
        pendingControlModeAction = null
    }

    private fun ensureNotificationPermissionForControlMode(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onGranted()
            return
        }
        val granted = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            onGranted()
            return
        }

        pendingControlModeAction = onGranted
        AlertDialog.Builder(requireContext())
            .setTitle("Allow alerts")
            .setMessage(
                "To start Control mode, the app needs notification permission so it can alert you " +
                    "about a detected fall on a locked or backgrounded phone."
            )
            .setIcon(R.mipmap.ic_launcher_round)
            .setNegativeButton("Not now") { _, _ ->
                pendingControlModeAction = null
                showNotificationPermissionDialog()
            }
            .setPositiveButton("Continue") { _, _ ->
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .show()
    }

    private fun showNotificationPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle("Notification permission required")
            .setMessage("Allow notifications so the app can warn you when a fall is detected.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Open settings") { _, _ ->
                val appSettingsIntent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + requireActivity().packageName)
                )
                startActivityForResult(appSettingsIntent, 123)
            }
            .show()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

}