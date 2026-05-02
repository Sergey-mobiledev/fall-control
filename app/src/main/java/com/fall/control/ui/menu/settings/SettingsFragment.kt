package com.fall.control.ui.menu.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import com.fall.control.R
import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.data.model.UserSettings
import com.fall.control.databinding.FragmentSettingsBinding
import com.fall.control.ui.base.BaseFragment
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.BUNDLE_KEY
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.NEGATIVE_RESULT
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.POSITIVE_RESULT
import com.fall.control.ui.home.dialog_microphone_permission.DialogMicrophonePermission.Companion.REQUEST_KEY
import com.fall.control.ui.home.first_start_dialog.utils.ItemsAlertMessageAdapter
import com.fall.control.ui.menu.settings.utils.SettingsView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(), SettingsView {

    override val viewModel by viewModel<SettingsViewModel>()
    private var doubleClick = true
    private var snackBar: Snackbar? = null
    private val itemsAlertMessageAdapter = ItemsAlertMessageAdapter(
        selectAlertMessage = {
            viewModel.selectSomeItem(it)
        },
        unselectAlertMessage = {
            viewModel.getItemsAlertMessageList()
        }
    )

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated() {
        binding.apply {
            itemsAlertMessageList.apply {
                isMotionEventSplittingEnabled = false
                adapter = itemsAlertMessageAdapter
                val itemAnimator = itemAnimator
                if (itemAnimator is DefaultItemAnimator)
                    itemAnimator.supportsChangeAnimations = false
            }
            buttonSet.setOnClickListener {
                setTextAlertMessage()
            }
        }
        viewModel.getFlowUserSettings()
        viewModel.getItemsAlertMessageList()
    }

    override fun setUserSettings(userSettings: UserSettings) {
        binding.apply {
            if (switchSoundLevelMeter.isChecked != userSettings.isSoundLevelMeterOn) {
                switchSoundLevelMeter.isChecked = userSettings.isSoundLevelMeterOn
            }
            if (userSettings.isSoundLevelMeterOn) {
                textOnOffSoundLevelMeter.apply {
                    text = getString(R.string.on)
                    setTextColor(getColor(requireContext(), R.color.caribbean_green))
                }
            } else {
                textOnOffSoundLevelMeter.apply {
                    text = getString(R.string.off)
                    setTextColor(getColor(requireContext(), R.color.saturated_pink))
                }
            }
            switchSoundLevelMeter.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkPermissionRecordAudio()
                } else {
                    viewModel.updateIsSoundLevelOn(false)
                }
            }

            if (switchAccelerometer.isChecked != userSettings.isAccelerometerOn)
                switchAccelerometer.isChecked =
                    userSettings.isAccelerometerOn
            if (userSettings.isAccelerometerOn) {
                textOnOffAccelerometer.apply {
                    text = getString(R.string.on)
                    setTextColor(getColor(requireContext(), R.color.caribbean_green))
                }
            } else {
                textOnOffAccelerometer.apply {
                    text = getString(R.string.off)
                    setTextColor(getColor(requireContext(), R.color.saturated_pink))
                }
            }

            switchAccelerometer.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.updateIsAccelerometerOn(true)
                } else {
                    viewModel.updateIsAccelerometerOn(false)
                }
            }
        }
    }

    override fun setItemsAlertMessageList(itemsAlertMessage: List<ItemAlertMessage>) {
        itemsAlertMessageAdapter.submitList(itemsAlertMessage)
    }

    override fun setTextAlertMessage() {
        val index = itemsAlertMessageAdapter.currentList.indexOfFirst { it.isSelected }
        if (index == -1) {
            if (binding.messageEditText.editableText.isEmpty()) {
                if (doubleClick) {
                    Toast.makeText(
                        requireContext(),
                        "Write alert message yourself or choose one of our variants",
                        Toast.LENGTH_SHORT
                    ).show()
                    doubleClick = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        doubleClick = true
                    }, 2000)
                }
            } else {
                viewModel.setTextAlertMessage(binding.messageEditText.editableText.toString())
            }
        } else {
            viewModel.setTextAlertMessage(itemsAlertMessageAdapter.currentList[index].message)
        }
    }

    override fun onBackPressed() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }

    private fun checkPermissionRecordAudio() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("111", "checkPermission: permission is granted")
            viewModel.updateIsSoundLevelOn()
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
            viewModel.updateIsSoundLevelOn()
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
        binding.switchSoundLevelMeter.isChecked = false
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
                snackBar?.dismiss()
                binding.snackBarBack.apply {
                    isVisible = false
                    it.setOnClickListener(null)
                }
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


}