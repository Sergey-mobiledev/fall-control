package com.fall.control.ui.menu

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.fall.control.R
import com.fall.control.databinding.FragmentMenuBinding

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private lateinit var binding: FragmentMenuBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMenuBinding.bind(view)
        binding.apply {
            buttonBack.setOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
            root.setOnClickListener {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }
}