package com.twproject.banyeomiji.view.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.view.LoadingDialog
import com.twproject.banyeomiji.view.datamodel.PetLocationData

class PetLocationViewModel(application: Application) : AndroidViewModel(application) {

    val db = Firebase.firestore

    //    private var petDataList = mutableListOf<PetLocationData>()
    private var petAllDataList = mutableListOf<PetLocationData>()

    private val _permissionCheck = MutableLiveData<Boolean>()
    private val _spinningPosition = MutableLiveData<Int>()
    private val _petAllLiveDataList = MutableLiveData<MutableList<PetLocationData>>()
    private val _spinnerCurrentItem = MutableLiveData<String>()
    private val _checkChange = MutableLiveData<Boolean>()
    private val _selectPosition = MutableLiveData<String>()
    private val _checkDirectionStoreMap = MutableLiveData<Boolean>()

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

    val permissionCheck: LiveData<Boolean> get() = _permissionCheck
    val spinningPosition: LiveData<Int> get() = _spinningPosition
    val petAllLiveDataList: LiveData<MutableList<PetLocationData>> get() = _petAllLiveDataList
    val spinnerCurrentItem: LiveData<String> get() = _spinnerCurrentItem
    val checkChange: LiveData<Boolean> get() = _checkChange
    val selectPosition: LiveData<String> get() = _selectPosition
    val checkDirectionStoreMap: LiveData<Boolean> get() = _checkDirectionStoreMap

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

    fun getAllLocationData() {
        _petAllLiveDataList.value = petAllDataList
    }

    suspend fun getLocationData(
        location: String, loadD: LoadingDialog, context: Context
    ) {
        db.collection("pet_location_data")
            .whereEqualTo("CTPRVN_NM", location)
            .get()
            .addOnSuccessListener {
                val dataGate = mutableListOf<PetLocationData>()
                val dataCafe = mutableListOf<PetLocationData>()
                val dataArt = mutableListOf<PetLocationData>()
                val dataSalon = mutableListOf<PetLocationData>()
                val dataMuseum = mutableListOf<PetLocationData>()
                val dataTool = mutableListOf<PetLocationData>()
                val dataRestaurant = mutableListOf<PetLocationData>()
                val dataTrip = mutableListOf<PetLocationData>()
                val dataManagement = mutableListOf<PetLocationData>()
                val dataSwimming = mutableListOf<PetLocationData>()

                for (document in it) {
                    val petLocationData = document.toObject(PetLocationData::class.java)
                    when (petLocationData.CTGRY_THREE_NM) {
                        "문예회관" -> dataGate.add(petLocationData)
                        "카페" -> dataCafe.add(petLocationData)
                        "미술관" -> dataArt.add(petLocationData)
                        "미용" -> dataSalon.add(petLocationData)
                        "박물관" -> dataMuseum.add(petLocationData)
                        "반려동물용품" -> dataTool.add(petLocationData)
                        "식당" -> dataRestaurant.add(petLocationData)
                        "여행지" -> dataTrip.add(petLocationData)
                        "위탁관리" -> dataManagement.add(petLocationData)
                        "펜션" -> dataSwimming.add(petLocationData)
                    }
                    petAllDataList.add(petLocationData)
                }

                _petLocationGateData.value = dataGate
                _petLocationCafeData.value = dataCafe
                _petLocationArtGalleryData.value = dataArt
                _petLocationPetSalonData.value = dataSalon
                _petLocationMuseumData.value = dataMuseum
                _petLocationToolsData.value = dataTool
                _petLocationRestaurantData.value = dataRestaurant
                _petLocationTripData.value = dataTrip
                _petLocationManagementData.value = dataManagement
                _petLocationSwimmingData.value = dataSwimming

                loadD.cancel()
            }
            .addOnFailureListener {
                Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                loadD.cancel()
            }
            .addOnCanceledListener {
                Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                loadD.cancel()
            }
    }

    fun setAllLocationData() {
        petAllDataList = mutableListOf()
    }

    fun setSpinningPosition(position: Int) {
        _spinningPosition.value = position
    }

    fun setPermissionCheck(boolean: Boolean) {
        _permissionCheck.value = boolean
    }

    fun setCurrentSpinnerItem(item: String) {
        _spinnerCurrentItem.value = item
    }

    fun setCheckChange(boolean: Boolean) {
        _checkChange.value = boolean
    }

    fun setSelectPosition(position: String) {
        _selectPosition.value = position
    }

    fun setCheckDirection(check: Boolean) {
        _checkDirectionStoreMap.value = check
    }

}


//suspend fun getLocationData(
//    category: String, location: String, loadD: LoadingDialog
//) {
//    db.collection("pet_location_data")
//        .whereEqualTo("CTGRY_THREE_NM", category)
//        .whereEqualTo("CTPRVN_NM", location)
//        .get()
//        .addOnSuccessListener {
//            for (document in it) {
//                val petLocationData = document.toObject(PetLocationData::class.java)
//                petDataList.add(petLocationData)
//                petAllDataList.add(petLocationData)
//            }
//            when(category) {
//                "문예회관" -> { _petLocationGateData.value = petDataList }
//                "카페" -> { _petLocationCafeData.value = petDataList }
//                "미술관" -> { _petLocationArtGalleryData.value = petDataList }
//                "미용" -> { _petLocationPetSalonData.value = petDataList }
//                "박물관" -> { _petLocationMuseumData.value = petDataList }
//                "반려동물용품" -> { _petLocationToolsData.value = petDataList }
//                "식당" -> { _petLocationRestaurantData.value = petDataList }
//                "여행지" -> { _petLocationTripData.value = petDataList }
//                "위탁관리" -> { _petLocationManagementData.value = petDataList }
//                "펜션" -> { _petLocationSwimmingData.value = petDataList }
//            }
//            petDataList = mutableListOf()
//
//            loadD.cancel()
//        }
//        .addOnFailureListener {
//            loadD.cancel()
//        }
//}
