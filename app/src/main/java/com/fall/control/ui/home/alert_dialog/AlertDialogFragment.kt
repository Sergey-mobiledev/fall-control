package com.fall.control.ui.home.alert_dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.fall.control.R
import com.fall.control.databinding.DialogFragmentAlertBinding
import com.fall.control.ui.home.alert_dialog.utils.AlertDialogView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AlertDialogFragment : DialogFragment(), AlertDialogView {

    private val itemFallHistoryId by lazy { arguments?.getInt(ITEM_FALL_HISTORY_ID) ?: 0 }
    private lateinit var binding: DialogFragmentAlertBinding
    override val width: Int = WindowManager.LayoutParams.MATCH_PARENT
    override val height: Int = WindowManager.LayoutParams.MATCH_PARENT
    override val viewModel by viewModel<AlertDialogViewModel>(parameters = {
        parametersOf(
            itemFallHistoryId
        )
    })

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
            window?.setDimAmount(0.75f)
        }
        setStyle(STYLE_NO_FRAME, android.R.style.Theme)
        binding = DialogFragmentAlertBinding.inflate(inflater, container, false)
        viewModel.attack(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragment = activity?.supportFragmentManager?.findFragmentById(R.id.full_screen_container_view)
        Log.d("111", "fragment $fragment")
        binding.apply {
            buttonRoot.setOnClickListener {
                onBackPressed()
                it.setOnClickListener(null)
            }
            buttonAlert.setOnClickListener {
                viewModel.shareMessage()
            }
        }
    }

    override fun sendMessage(message: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
        dismiss()
    }

    override fun onBackPressed() {
        dismiss()
    }

    companion object {
        const val ITEM_FALL_HISTORY_ID = "ITEM_FALL_HISTORY_ID"
    }
}