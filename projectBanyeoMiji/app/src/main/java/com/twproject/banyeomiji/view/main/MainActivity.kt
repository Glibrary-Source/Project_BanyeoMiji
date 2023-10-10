package com.twproject.banyeomiji.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivityMainBinding
import com.twproject.banyeomiji.vbutility.onThrottleClick
import com.twproject.banyeomiji.view.login.LoginActivity
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        petLocationViewModel = ViewModelProvider(this)[PetLocationViewModel::class.java]
        petLocationViewModel.setPermissionCheck(true)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavBar.setupWithNavController(navController)

        MobileAds.initialize(this)

        val mAdView = binding.adViewBanner
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        binding.btnUserAccount.onThrottleClick {
            ButtonAnimation().startAnimation(it)
            CoroutineScope(Main).launch {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                delay(300)
                startActivity(intent)
            }
        }
        initUserNickName()
    }

    private fun initUserNickName() {
        val userUid = GoogleObjectAuth.mAuth.currentUser?.uid
        if (userUid != null) {
            db.collection("user_db").document(userUid)
                .get()
                .addOnSuccessListener { user ->
                    MyGlobals.instance!!.userNickName = user.data!!["nickname"].toString()
                }
        }
    }


}