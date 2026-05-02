package com.fall.control.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.fall.control.R

class Dialog : DialogFragment() {

    private val message by lazy { arguments?.getString(MESSAGE) ?: "null" }

    override fun onStart() {
        super.onStart()
        val myDialog = dialog as AlertDialog
        myDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle(message)
            .setPositiveButton("Ok") { _, i ->
                dismiss()
            }
            .create()
    }

    companion object {
        const val MESSAGE = "dialog_fragment_message_id"
        const val TAG = "dialog_fragment"
    }
}