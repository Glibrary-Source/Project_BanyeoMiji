package com.twproject.banyeomiji.view.util

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding
import com.twproject.banyeomiji.view.adapter.LocationDataListAdapter
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel

class LocationListViewManager {
    fun setLocationDataList(
        categoryData: String,
        petLocationViewModel: PetLocationViewModel,
        activity: FragmentActivity,
        mContext: Context,
        rcLocationListView: RecyclerView,
        binding: FragmentLocationListBinding
    ) {
        when (categoryData) {
            "문예회관" -> { petLocationViewModel.petLocationGateData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationGateData.value!!, mContext, binding)
            } }
            "카페" -> { petLocationViewModel.petLocationCafeData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationCafeData.value!!, mContext, binding)
            } }
            "미술관" -> { petLocationViewModel.petLocationArtGalleryData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationArtGalleryData.value!!, mContext, binding)
            } }
            "미용" -> { petLocationViewModel.petLocationPetSalonData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationPetSalonData.value!!, mContext, binding)
            } }
            "박물관" -> { petLocationViewModel.petLocationMuseumData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationMuseumData.value!!, mContext, binding)
            } }
            "반려동물용품" -> { petLocationViewModel.petLocationToolsData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationToolsData.value!!, mContext, binding)
            } }
            "식당" -> { petLocationViewModel.petLocationRestaurantData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationRestaurantData.value!!, mContext, binding)
            } }
            "여행지" -> { petLocationViewModel.petLocationTripData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationTripData.value!!, mContext, binding)
            } }
            "위탁관리" -> { petLocationViewModel.petLocationManagementData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationManagementData.value!!, mContext, binding)
            } }
            "펜션" -> { petLocationViewModel.petLocationSwimmingData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationSwimmingData.value!!, mContext, binding)
            } }
        }
    }
}