package com.twproject.banyeomiji.view.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivitySplashBinding
import com.twproject.banyeomiji.view.splash.utils.NetworkManager
import com.twproject.banyeomiji.view.splash.utils.SplashRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var splashRemoteConfig: SplashRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Main).launch {
            var networkState: Boolean
            withContext(IO) { networkState = NetworkManager.checkNetworkState(this@SplashActivity) }
            if (!networkState) {
                delay(2000)
                Toast.makeText(this@SplashActivity, "네트워크 상태를 확인해주세요", Toast.LENGTH_SHORT).show()
                this@SplashActivity.finish()
            } else {
                splashRemoteConfig = SplashRemoteConfig(this@SplashActivity)
                splashRemoteConfig.initRemoteConfig()
            }
        }
    }
}