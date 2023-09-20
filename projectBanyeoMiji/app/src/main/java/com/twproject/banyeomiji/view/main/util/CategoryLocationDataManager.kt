package com.twproject.banyeomiji.view.main.util

import android.content.Context
import com.twproject.banyeomiji.view.main.LoadingDialog
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryLocationDataManager {
    fun selectLocationData(
        petLocationViewModel: PetLocationViewModel,
        location: String,
        loadD: LoadingDialog,
        context: Context
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            petLocationViewModel.getLocationData(location, loadD, context)
            petLocationViewModel.setAllLocationData()
//            petLocationViewModel.getLocationData("문예회관", location, loadD)
//            petLocationViewModel.getLocationData("카페", location, loadD)
//            petLocationViewModel.getLocationData("미술관", location, loadD)
//            petLocationViewModel.getLocationData("미용", location, loadD)
//            petLocationViewModel.getLocationData("박물관", location, loadD)
//            petLocationViewModel.getLocationData("반려동물용품", location, loadD)
//            petLocationViewModel.getLocationData("식당", location, loadD)
//            petLocationViewModel.getLocationData("여행지", location, loadD)
//            petLocationViewModel.getLocationData("위탁관리", location, loadD)
//            petLocationViewModel.getLocationData("펜션", location, loadD)
        }
    }
}