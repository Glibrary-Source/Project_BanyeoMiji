package com.twproject.banyeomiji.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.twproject.banyeomiji.BuildConfig
import com.google.firebase.ktx.Firebase
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
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var userSelectManager: UserSelectManager
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userSelectManager = UserSelectManager(this.dataStore)

        NaverIdLoginSDK.initialize(
            this,
            BuildConfig.NAVER_CLIENT_ID,
            BuildConfig.NAVER_CLIENT_SECRET,
            getString(R.string.app_package_name)
        )

        petLocationViewModel = ViewModelProvider(this)[PetLocationViewModel::class.java]
        petLocationViewModel.setPermissionCheck(true)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavBar.setupWithNavController(navController)

        val mAdView = binding.adViewBanner
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        setLoginState()
    }

    private fun setLoginState() {
        CoroutineScope(IO).launch {
            val naverLogin = when (NaverIdLoginSDK.getState().name) {
                "NEED_LOGIN" -> false
                "NEED_INIT" -> false
                "NEED_REFRESH_TOKEN" -> false
                else -> true
            }

            var googleLogin = GoogleObjectAuth.getFirebaseAuth().currentUser != null
            if (googleLogin) {
                db.collection("user_db")
                    .document(GoogleObjectAuth.getFirebaseAuth().currentUser!!.uid)
                    .get()
                    .addOnSuccessListener {
                        val login = it.data
                        googleLogin = login != null
                        if(googleLogin) {
                            MyGlobals.instance!!.userLogin = 1
                            MyGlobals.instance!!.userDataCheck = 1
                        } else {
                            MyGlobals.instance!!.userLogin = 0
                            MyGlobals.instance!!.userDataCheck = 0
                            GoogleObjectAuth.getFirebaseAuth().signOut()
                        }
                    }
            }
            if (googleLogin || naverLogin) {
                MyGlobals.instance!!.userLogin = 1
            } else {
                MyGlobals.instance!!.userLogin = 0
            }
        }
    }
}