package com.twproject.banyeomiji.view.main.util

import android.Manifest

class PermissionManager {
    fun getPermissionList(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }
}