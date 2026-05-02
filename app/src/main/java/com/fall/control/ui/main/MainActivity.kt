package com.fall.control.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import com.fall.control.R
import com.fall.control.data.service.Extensions.popBackStackInChildContainer
import com.fall.control.data.service.Extensions.removeFragmentFromChildContainer
import com.fall.control.databinding.ActivityMainBinding
import com.fall.control.ui.home.alert_dialog.AlertDialogFragment.Companion.ITEM_FALL_HISTORY_ID
import com.fall.control.ui.main.utils.MainView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), MainView {

    private var doubleClick = false
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainViewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        WindowCompat.getInsetsController(window, window.decorView)?.let { controller ->
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.show(WindowInsetsCompat.Type.statusBars())
            controller.show(WindowInsetsCompat.Type.navigationBars())
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainFragmentContainerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mainViewModel.attack(this)
        this.onBackPressedDispatcher.addCallback(this, oBackPressedDispatcher)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("111", "onDestroy mainActivity")
        mainViewModel.detach()
    }

    private val oBackPressedDispatcher by lazy {
        object : OnBackPressedCallback(true) {
            @SuppressLint("RestrictedApi")
            override fun handleOnBackPressed() {
                val currentFragmentId = mainViewModel.getCurrentFragmentId()
                Log.d("111", "currentFragmentId $currentFragmentId")
                when (currentFragmentId) {
                    R.id.infoFragment, R.id.settingsFragment, R.id.historyFragment, R.id.historyOneFragment -> {
                        popBackStackInChildContainer(binding.root)
                        return
                    }

                    R.id.menuHomeFragment -> {
                        removeFragmentFromChildContainer()
                        mainViewModel.emitHomeFragment()
                        return
                    }

                    R.id.startFragment -> {
                        finish()
                        return
                    }

                    R.id.homeFragment -> {
                        if (doubleClick) {
                            finish()
                            return
                        }
                        doubleClick = true
                        Toast.makeText(
                            this@MainActivity,
                            "Click BACK against to exit",
                            Toast.LENGTH_SHORT
                        ).show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            doubleClick = false
                        }, 2000)
                    }

                    else -> {
                        findNavController(R.id.main_fragment_container_view).navigateUp()
                    }
                }
            }
        }
    }

    override fun setMainBackGround() {
        window?.apply {
            setBackgroundDrawableResource(R.drawable.main)
        }
    }

    override fun showAlertFragment(itemFallHistoryId: Int) {
        Log.d("111", "showAlertFragment in activity")
        val navController = findNavController(R.id.main_fragment_container_view)
        if (navController.currentDestination?.id != R.id.alertDialogFragment){
            navController.navigate(
                R.id.alertDialogFragment,
                bundleOf(ITEM_FALL_HISTORY_ID to itemFallHistoryId)
            )
            Log.d("111", "showAlertFragment in activity SHOOOOWOWOWOW")
        }
    }
}