package com.twproject.banyeomiji.view.util

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.twproject.banyeomiji.view.LoadingDialog
import com.twproject.banyeomiji.view.UserSelectManager
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategorySpinnerManager(
    private val spinner: Spinner,
    private val userManager: UserSelectManager) {
    fun setSpinnerSelect(
        items: Array<String>,
        mContext: Context,
        petLocationViewModel: PetLocationViewModel,
        categoryLocationDataManager: CategoryLocationDataManager,
    ) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val loadD = LoadingDialog(mContext)
                if (petLocationViewModel.spinningPosition.value != position){
                    loadD.show()
                    categoryLocationDataManager.selectLocationData(
                        petLocationViewModel,
                        items[position],
                        loadD,
                        mContext
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                         userManager.selectUser(position)
                    }
                    petLocationViewModel.setCheckChange(true)
                }
                petLocationViewModel.setSpinningPosition(position)
                petLocationViewModel.setCurrentSpinnerItem(spinner.selectedItem.toString())

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun setStartSpinnerPosition() {
        CoroutineScope(Dispatchers.IO).launch {
            userManager.userSelectFlow.collect {
                if (it != null) {
                    withContext(Dispatchers.Main) {
                        spinner.setSelection(it)
                    }
                }
            }
        }
    }
}