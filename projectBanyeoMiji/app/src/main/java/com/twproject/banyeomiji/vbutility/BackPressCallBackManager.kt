package com.twproject.banyeomiji.vbutility

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.main.FragmentReviewDirections

object BackPressCallBackManager {
    fun setBackPressCallBack(activity: FragmentActivity, context: Context) : OnBackPressedCallback {
        var backPressTime: Long = 0
        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressTime + 3000 > System.currentTimeMillis()) {
                    activity.finish()
                } else {
                    Toast.makeText(context , "한번 더 뒤로가기 버튼을 누르면 종료됩니다", Toast.LENGTH_SHORT)
                        .show()
                }
                backPressTime = System.currentTimeMillis()
            }
        }
    }

}