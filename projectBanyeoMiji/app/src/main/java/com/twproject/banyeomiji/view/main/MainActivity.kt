package com.twproject.banyeomiji.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.navercorp.nid.NaverIdLoginSDK
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivityMainBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import com.twproject.banyeomiji.vbutility.onThrottleClick
import com.twproject.banyeomiji.view.login.LoginActivity
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var userSelectManager: UserSelectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userSelectManager = UserSelectManager(this.dataStore)
        NaverIdLoginSDK.initialize(this, getString(R.string.naver_client_id), getString(R.string.naver_client_secret), "com.twproject.banyeomiji")

        petLocationViewModel = ViewModelProvider(this)[PetLocationViewModel::class.java]
        petLocationViewModel.setPermissionCheck(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavBar.setupWithNavController(navController)

        binding.btnUserAccount.onThrottleClick {
            ButtonAnimation().startAnimation(it)
            CoroutineScope(Main).launch {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                delay(300)
                startActivity(intent)
            }
        }

        val mAdView = binding.adViewBanner
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        setLoginState()
    }

    private fun setLoginState() {
        var googleLogin = true

        if(GoogleObjectAuth.getFirebaseAuth().currentUser == null) {
            googleLogin = false
        }

        val naverLogin: Boolean = when(NaverIdLoginSDK.getState().name) {
            "NEED_LOGIN" -> false
            "NEED_INIT" -> false
            "NEED_REFRESH_TOKEN" -> false
            else -> true
        }

        if( !googleLogin && !naverLogin ) {
            MyGlobals.instance!!.userLogin = 0
        }
    }
}