package com.twproject.banyeomiji.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivitySplashBinding
import com.twproject.banyeomiji.view.main.MainActivity
import com.twproject.banyeomiji.view.splash.utils.SplashRemoteConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        splashRemoteConfig = SplashRemoteConfig(this)
        splashRemoteConfig.initRemoteConfig()

//        val intent = Intent(this, MainActivity::class.java)
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(2000)
//            startActivity(intent)
//            finish()
//        }

    }
}