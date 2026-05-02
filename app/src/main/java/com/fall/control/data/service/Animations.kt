package com.fall.control.data.service

import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import com.fall.control.R

object Animations {

    fun showViewFromBottom(view: View) {
        view.apply {
            if (!isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.show_from_bottom
                    )
                )
            }
            isClickable = true
            isVisible = true
        }
    }

    fun hideViewToBottom(view: View) {
        view.apply {
            if (isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.hide_to_bottom
                    )
                )
            }
            isClickable = false
            isVisible = false
        }
    }

    fun showViewFromTop(view: View) {
        view.apply {
            if (!isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.show_from_top
                    )
                )
            }
            isClickable = true
            isVisible = true
        }
    }


    fun hideViewToTop(view: View) {
        view.apply {
            if (isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.hide_to_top
                    )
                )
            }
            isClickable = false
            isVisible = false
        }
    }

    fun showViewFadeIn(view: View) {
        view.apply {
            if (!isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.fade_in
                    )
                )
            }
            isClickable = true
            isVisible = true
        }
    }

    fun hideViewFadeOut(view: View) {
        view.apply {
            if (isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.fade_out
                    )
                )
            }
            isClickable = false
            isVisible = false
        }
    }

    fun showViewScaleIn(view: View) {
        view.apply {
            if (!isVisible){
                startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.scale_in
                    )
                )
            }
            isClickable = true
            isVisible = true
        }
    }
}