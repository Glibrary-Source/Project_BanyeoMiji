package com.twproject.banyeomiji.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.view.datamodel.PetCategoryData
import com.twproject.banyeomiji.view.datamodel.PetLocationData
import kotlinx.coroutines.launch

class PetLocationViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val petDataList = mutableListOf<PetLocationData>()
    private val petCategoryData = mutableListOf<PetCategoryData>()

    private val _petLocationData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petDataCategory = MutableLiveData<MutableList<PetCategoryData>>()

    val petLocationData: LiveData<MutableList<PetLocationData>> get() = _petLocationData
    val petDataCategory: LiveData<MutableList<PetCategoryData>> get() = _petDataCategory

//    init {
//        getLocationData()
//    }

    //viewModel scope 사용시
//    private fun getLocationData() {
//        viewModelScope.launch {
//            db.collection("pet_location_data")
//                .whereEqualTo("CTPRVN_NM", "서울특별시")
//                .whereEqualTo("FCLTY_INFO_DC", "애견카페")
//                .get()
//                .addOnSuccessListener {
//                    for (document in it) {
//                        val petLocationData = document.toObject(PetLocationData::class.java)
//                        petDataList.add(petLocationData)
//                    }
//                    _petLocationData.value = petDataList
//                }
//                .addOnFailureListener { exception ->
//                    Log.d("testTag", "Error: $exception")
//                }
//        }
//    }

    suspend fun getLocationDataCoroutine() {
        db.collection("pet_location_data")
            .whereEqualTo("CTPRVN_NM", "서울특별시")
            .whereEqualTo("CTGRY_THREE_NM", "카페")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val petLocationData = document.toObject(PetLocationData::class.java)
                    petDataList.add(petLocationData)
                }
                _petLocationData.value = petDataList
            }
            .addOnFailureListener { exception ->
                Log.d("testTag", "Error: $exception")
            }
    }

//    suspend fun getLocationCategory() {
//        db.collection("pet_data_category")
//            .get()
//            .addOnSuccessListener {
//                for(document in it) {
//                    val tempPetCategory = document.toObject(PetCategoryData::class.java)
//                    petCategoryData.add(tempPetCategory)
//                }
//                _petDataCategory.value = petCategoryData
//            }
//            .addOnFailureListener { exception ->
//                Log.d("testTag", "Error: $exception")
//            }
//    }

}