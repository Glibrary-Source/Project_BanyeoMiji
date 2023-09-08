package com.twproject.banyeomiji.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding
import com.twproject.banyeomiji.view.adapter.LocationDataListAdapter
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel

class FragmentLocationList : Fragment() {

    private lateinit var binding: FragmentLocationListBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private val categoryData by navArgs<FragmentLocationListArgs>()
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel = ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationListBinding.inflate(inflater)

        val rcLocationListView = binding.rcLocationList
        val activity = requireActivity()
        rcLocationListView.setHasFixedSize(true)

        when (categoryData.CategoryName) {
            "문예회관" -> { petLocationViewModel.petLocationGateData.observe(activity) {
                    rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationGateData.value!!, mContext)
                } }
            "카페" -> { petLocationViewModel.petLocationCafeData.observe(activity) {
                    rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationCafeData.value!!, mContext)
                } }
            "미술관" -> { petLocationViewModel.petLocationArtGalleryData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationArtGalleryData.value!!, mContext)
            } }
            "미용" -> { petLocationViewModel.petLocationPetSalonData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationPetSalonData.value!!, mContext)
            } }
            "박물관" -> { petLocationViewModel.petLocationMuseumData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationMuseumData.value!!, mContext)
            } }
            "반려동물용품" -> { petLocationViewModel.petLocationToolsData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationToolsData.value!!, mContext)
            } }
            "식당" -> { petLocationViewModel.petLocationRestaurantData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationRestaurantData.value!!, mContext)
            } }
            "여행지" -> { petLocationViewModel.petLocationTripData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationTripData.value!!, mContext)
            } }
            "위탁관리" -> { petLocationViewModel.petLocationManagementData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationManagementData.value!!, mContext)
            } }
            "펜션" -> { petLocationViewModel.petLocationSwimmingData.observe(activity) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationSwimmingData.value!!, mContext)
            } }
        }

        rcLocationListView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

}