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
            petLocationViewModel.getLocationData(
                location,
                loadD,
                context
            )
            petLocationViewModel.setAllLocationData()
        }
    }
}