package com.fall.control.data.service

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.fall.control.R

object Extensions {

    fun AppCompatActivity.popBackStackInChildContainer(root: View){
        val fragmentContainer =
            root.findViewById<View>(R.id.menu_fragment_container_view)
        val childNavController = Navigation.findNavController(fragmentContainer)
        childNavController.popBackStack()
    }

    fun AppCompatActivity.removeFragmentFromChildContainer(){
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.full_screen_container_view)
        if (fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }
    }
}