package com.twproject.banyeomiji.view.util

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.twproject.banyeomiji.R

class NavigationOptionsManager(private val navController: NavController) {
    fun setBottomTransformOption(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_from_right)
            .setExitAnim(R.anim.exit_to_right)
            .setPopEnterAnim(R.anim.enter_from_right)
            .setPopExitAnim(R.anim.exit_to_right)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()
    }

}