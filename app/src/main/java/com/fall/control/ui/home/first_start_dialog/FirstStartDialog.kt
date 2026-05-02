package com.fall.control.ui.home.first_start_dialog

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.fall.control.data.model.ItemAlertMessage
import com.fall.control.databinding.DialogFragmentFirstStartBinding
import com.fall.control.ui.base.BaseDialogFragment
import com.fall.control.ui.home.first_start_dialog.utils.FirstStartView
import com.fall.control.ui.home.first_start_dialog.utils.ItemsAlertMessageAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FirstStartDialog :
    BaseDialogFragment<DialogFragmentFirstStartBinding>(),
    FirstStartView {

    override val viewModel by viewModel<FirstStartViewModel>()
    override val width: Int = WindowManager.LayoutParams.MATCH_PARENT
    override val height: Int = WindowManager.LayoutParams.WRAP_CONTENT

    private var doubleClick = true
    private val itemsAlertMessageAdapter = ItemsAlertMessageAdapter(
        selectAlertMessage = {
            viewModel.selectSomeItem(it)
        },
        unselectAlertMessage = {
            viewModel.getData()
        }
    )

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogFragmentFirstStartBinding {
        return DialogFragmentFirstStartBinding.inflate(inflater, container, false)
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
        viewModel.getData()
    }

    override fun setData(itemAlertMessages: List<ItemAlertMessage>) {
        itemsAlertMessageAdapter.submitList(itemAlertMessages)
    }

    override fun setTextAlertMessage() {
        val index = itemsAlertMessageAdapter.currentList.indexOfFirst { it.isSelected }
        if (index == -1) {
            if (binding.messageEditText.editableText.isEmpty()) {
                if (doubleClick){
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
    }