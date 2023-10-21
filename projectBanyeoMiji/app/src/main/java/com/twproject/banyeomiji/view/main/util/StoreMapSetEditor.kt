package com.twproject.banyeomiji.view.main.util

import android.util.Log
import androidx.navigation.NavController
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.main.FragmentStoreMapDirections
import com.twproject.banyeomiji.view.main.data.LocationLatLng
import com.twproject.banyeomiji.view.main.datamodel.PetLocationData
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel

class StoreMapSetEditor(
    private val naverFragmentMap: NaverMap
) {

    fun uiSetting() {
        val uiSetting = naverFragmentMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
        uiSetting.isLogoClickEnabled = false
    }
    fun setMaxMinZoom() {
        naverFragmentMap.minZoom = 8.0
    }

    fun setLocationSource(locationSource: FusedLocationSource) {
        naverFragmentMap.locationSource = locationSource
    }

    fun setMapExtent() {
        naverFragmentMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0) )
    }

    fun getMarkerIcon(storeCategory: String): OverlayImage {
        return when (storeCategory) {
            "문예회관" -> OverlayImage.fromResource(R.drawable.icon_map_korea_gate)
            "카페" -> OverlayImage.fromResource(R.drawable.icon_map_cafe)
            "미술관" -> OverlayImage.fromResource(R.drawable.icon_map_art_gallery)
            "미용" -> OverlayImage.fromResource(R.drawable.icon_map_salon)
            "박물관" -> OverlayImage.fromResource(R.drawable.icon_map_museum)
            "반려동물용품" -> OverlayImage.fromResource(R.drawable.icon_map_tools)
            "식당" -> OverlayImage.fromResource(R.drawable.icon_map_restaurant)
            "여행지" -> OverlayImage.fromResource(R.drawable.icon_map_trip)
            "위탁관리" -> OverlayImage.fromResource(R.drawable.icon_map_management)
            "펜션" -> OverlayImage.fromResource(R.drawable.icon_map_hotel)
            else -> {
                OverlayImage.fromResource(R.drawable.img_category_item_korea_gate)
            }
        }
    }

    fun setMarkerInfoWindowOnClickListener(
        document: PetLocationData,
        navController: NavController,
        petLocationViewModel: PetLocationViewModel
    ): Overlay.OnClickListener {
        return Overlay.OnClickListener {
            val action =
                FragmentStoreMapDirections.actionFragmentStoreMapToFragmentLocationList(document.CTGRY_THREE_NM)
            navController.navigate(action)
            petLocationViewModel.setSelectPosition(document.FCLTY_NM)
            petLocationViewModel.setCheckDirection(true)
            true
        }
    }

    fun locationChangeMoveCamera(
        petLocationViewModel: PetLocationViewModel,
        locationLatLng: LocationLatLng
    ) {
        try{
            if (petLocationViewModel.checkChange.value!!) {
                val location = petLocationViewModel.spinnerCurrentItem.value!!
                val currentLocationLatLng = locationLatLng.getLatLng(location)
                val cameraUpdate = CameraUpdate.scrollAndZoomTo(currentLocationLatLng, 12.0)
                naverFragmentMap.moveCamera(cameraUpdate)
                petLocationViewModel.setCheckChange(false)
            }
        } catch (_: Exception) {}
    }

}