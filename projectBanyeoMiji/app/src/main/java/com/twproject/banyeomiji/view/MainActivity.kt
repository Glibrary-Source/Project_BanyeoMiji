package com.twproject.banyeomiji.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivityMainBinding
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var petLocationViewModel: PetLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        petLocationViewModel = ViewModelProvider(this)[PetLocationViewModel::class.java]

        val location = "경기도"
        CoroutineScope(IO).launch {
            petLocationViewModel.getLocationData("문예회관", location )
            petLocationViewModel.getLocationData("카페", location)
            petLocationViewModel.getLocationData("미술관", location)
            petLocationViewModel.getLocationData("미용", location)
            petLocationViewModel.getLocationData("박물관", location)
            petLocationViewModel.getLocationData("반려동물용품", location)
            petLocationViewModel.getLocationData("식당", location)
            petLocationViewModel.getLocationData("여행지", location)
            petLocationViewModel.getLocationData("위탁관리", location)
            petLocationViewModel.getLocationData("펜션", location)
            petLocationViewModel.getLocationData("호텔", location)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
    }
}

