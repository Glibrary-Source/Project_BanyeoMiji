package com.twproject.banyeomiji.view.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivitySplashBinding
import com.twproject.banyeomiji.view.splash.utils.NetworkManager
import com.twproject.banyeomiji.view.splash.utils.SplashRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashRemoteConfig: SplashRemoteConfig


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)


        if(!NetworkManager.checkNetworkState(this)) {
            CoroutineScope(Main).launch{
                delay(2000)
                Toast.makeText(this@SplashActivity, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                this@SplashActivity.finish()
            }
        } else {
            splashRemoteConfig = SplashRemoteConfig(this)
            splashRemoteConfig.initRemoteConfig()
        }
    }
}