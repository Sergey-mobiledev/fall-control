package com.fall.control.ui.base

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.fall.control.R

abstract class BaseDialogFragmentPermission : DialogFragment(), BaseView {

    protected abstract val viewModel: BaseViewModel
    protected abstract val title: String
    protected abstract val message: String

    abstract fun onPositiveButtonClick()
    abstract fun onNegativeButtonClick()
    abstract fun onViewCreated()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.attack(this)
        onViewCreated()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setIcon(R.mipmap.ic_launcher_round)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { _, i ->
                onPositiveButtonClick()
                dismiss()
            }
            .setNegativeButton("Cancel") { _, i ->
                onNegativeButtonClick()
                dismiss()
            }
            .create()
    }

    override fun onStart() {
        super.onStart()
        val myDialog = dialog as AlertDialog
        myDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        myDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.emitHomeFragmentAndDetach()
    }
}