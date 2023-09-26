package com.twproject.banyeomiji.vbutility

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View

class ButtonAnimation {

    fun startAnimation(buttonView: View) {
        // Define the initial width and height
        val initialWidth = buttonView.width
        val initialHeight = buttonView.height

        // Define the animation duration in milliseconds
        val animationDuration = 300L  // You can adjust this value as needed

        // Create a ValueAnimator for the width and height
        val widthAnimator = ValueAnimator.ofInt(buttonView.width, (initialWidth * 0.9).toInt())
        val heightAnimator =
            ValueAnimator.ofInt(buttonView.height, (initialHeight * 0.9).toInt())

        // Set the animation duration
        widthAnimator.duration = animationDuration
        heightAnimator.duration = animationDuration

        // Update the layout params as the animation progresses
        widthAnimator.addUpdateListener { animation ->
            val newWidth = animation.animatedValue as Int
            val params = buttonView.layoutParams
            params.width = newWidth
            buttonView.layoutParams = params
        }

        heightAnimator.addUpdateListener { animation ->
            val newHeight = animation.animatedValue as Int
            val params = buttonView.layoutParams
            params.height = newHeight
            buttonView.layoutParams = params
        }

        // Start the animations
        widthAnimator.start()
        heightAnimator.start()

        // Set up an animation listener to return to the original size when the animation ends
        widthAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // Create a new animation to return to the original size
                val returnWidthAnimator =
                    ValueAnimator.ofInt(buttonView.width, initialWidth)
                val returnHeightAnimator =
                    ValueAnimator.ofInt(buttonView.height, initialHeight)

                returnWidthAnimator.duration = animationDuration
                returnHeightAnimator.duration = animationDuration

                returnWidthAnimator.addUpdateListener { animation1 ->
                    val newWidth = animation1.animatedValue as Int
                    val params = buttonView.layoutParams
                    params.width = newWidth
                    buttonView.layoutParams = params
                }

                returnHeightAnimator.addUpdateListener { animation2 ->
                    val newHeight = animation2.animatedValue as Int
                    val params = buttonView.layoutParams
                    params.height = newHeight
                    buttonView.layoutParams = params
                }

                returnWidthAnimator.start()
                returnHeightAnimator.start()
            }

            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

}