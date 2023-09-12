package com.twproject.banyeomiji.view.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.view.LoadingDialog
import com.twproject.banyeomiji.view.datamodel.PetLocationData

class PetLocationViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Firebase.firestore
    private var petDataList = mutableListOf<PetLocationData>()

    private val _spinningPosition = MutableLiveData<Int>()

    private val _petLocationGateData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationCafeData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationArtGalleryData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationPetSalonData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationMuseumData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationToolsData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationRestaurantData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationTripData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationManagementData = MutableLiveData<MutableList<PetLocationData>>()
    private val _petLocationSwimmingData = MutableLiveData<MutableList<PetLocationData>>()

    val spinningPosition: LiveData<Int> get() = _spinningPosition

    val petLocationGateData: LiveData<MutableList<PetLocationData>> get() = _petLocationGateData
    val petLocationCafeData: LiveData<MutableList<PetLocationData>> get() = _petLocationCafeData
    val petLocationArtGalleryData: LiveData<MutableList<PetLocationData>> get() = _petLocationArtGalleryData
    val petLocationPetSalonData: LiveData<MutableList<PetLocationData>> get() = _petLocationPetSalonData
    val petLocationMuseumData: LiveData<MutableList<PetLocationData>> get() = _petLocationMuseumData
    val petLocationToolsData: LiveData<MutableList<PetLocationData>> get() = _petLocationToolsData
    val petLocationRestaurantData: LiveData<MutableList<PetLocationData>> get() = _petLocationRestaurantData
    val petLocationTripData: LiveData<MutableList<PetLocationData>> get() = _petLocationTripData
    val petLocationManagementData: LiveData<MutableList<PetLocationData>> get() = _petLocationManagementData
    val petLocationSwimmingData: LiveData<MutableList<PetLocationData>> get() = _petLocationSwimmingData

    init {
        setSpinningPosition(-1)
    }

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

    suspend fun getLocationData(
        category: String, location: String, loadD: LoadingDialog
    ) {
        db.collection("pet_location_data")
            .whereEqualTo("CTGRY_THREE_NM", category)
            .whereEqualTo("CTPRVN_NM", location)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val petLocationData = document.toObject(PetLocationData::class.java)
                    petDataList.add(petLocationData)
                }
                when(category) {
                    "문예회관" -> { _petLocationGateData.value = petDataList }
                    "카페" -> { _petLocationCafeData.value = petDataList }
                    "미술관" -> { _petLocationArtGalleryData.value = petDataList }
                    "미용" -> { _petLocationPetSalonData.value = petDataList }
                    "박물관" -> { _petLocationMuseumData.value = petDataList }
                    "반려동물용품" -> { _petLocationToolsData.value = petDataList }
                    "식당" -> { _petLocationRestaurantData.value = petDataList }
                    "여행지" -> { _petLocationTripData.value = petDataList }
                    "위탁관리" -> { _petLocationManagementData.value = petDataList }
                    "펜션" -> { _petLocationSwimmingData.value = petDataList }
                }
                petDataList = mutableListOf()

                loadD.cancel()
            }
            .addOnFailureListener {
                loadD.cancel()
            }
    }

    fun setSpinningPosition(position: Int) {
        _spinningPosition.value = position
    }

}