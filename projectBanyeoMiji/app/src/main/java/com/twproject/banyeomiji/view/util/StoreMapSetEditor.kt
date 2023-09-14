package com.twproject.banyeomiji.view.util

import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource

class StoreMapSetEditor(private val naverFragmentMap: NaverMap) {

    fun uiSetting() {
        val uiSetting = naverFragmentMap.uiSettings
        uiSetting.isLocationButtonEnabled = true
        uiSetting.isLogoClickEnabled = false
    }
    fun setMaxMinZoom() {
        naverFragmentMap.minZoom = 15.0
    }

    fun setLocationSource(locationSource: FusedLocationSource) {
        naverFragmentMap.locationSource = locationSource
    }


}