package com.twproject.banyeomiji.view.data

import com.naver.maps.geometry.LatLng

class LocationLatLng {
    fun getLatLng(location:String): LatLng {
        val locationMap = mutableMapOf(
            "서울특별시" to LatLng(37.5666102, 126.9783881),
            "강원도" to LatLng(37.348326, 127.928925),
            "경기도" to LatLng(37.344375, 126.907408),
            "경상남도" to LatLng(35.181972, 128.651552),
            "경상북도" to LatLng(35.856416, 128.772104),
            "광주광역시" to LatLng(35.151650, 126.834937),
            "대구광역시" to LatLng(35.860123, 128.627068),
            "대전광역시" to LatLng(36.361200, 127.406081),
            "대전광역시" to LatLng(36.361200, 127.406081),
            "부산광역시" to LatLng(35.166223, 129.048755),
            "세종특별자치시" to LatLng(36.502973, 127.262818),
            "울산광역시" to LatLng(35.537936, 129.278494),
            "인천광역시" to LatLng(37.446467, 126.730393),
            "전라남도" to LatLng(34.975987, 126.740907),
            "전라북도" to LatLng(35.828776, 127.130502),
            "제주특별자치도" to LatLng(33.380662, 126.570323),
            "충청남도" to LatLng(36.581054, 126.681965),
            "충청북도" to LatLng(36.624245, 127.527759),
        )
        return locationMap[location]!!
    }

}