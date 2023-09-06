package com.twproject.banyeomiji.view

import android.content.Context
import android.os.Bundle
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class FragmentLocationList : Fragment() {

    private lateinit var binding: FragmentLocationListBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private val categoryData by navArgs<FragmentLocationListArgs>()
    lateinit var mContext: Context

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
        rcLocationListView.setHasFixedSize(true)

        when (categoryData.CategoryName) {
            "문예회관" -> { petLocationViewModel.petLocationGateData.observe(requireActivity()) {
                    rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationGateData.value!!, requireContext())
                } }
            "카페" -> { petLocationViewModel.petLocationCafeData.observe(requireActivity()) {
                    rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationCafeData.value!!, requireContext())
                } }
            "미술관" -> { petLocationViewModel.petLocationArtGalleryData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationArtGalleryData.value!!, requireContext())
            } }
            "미용" -> { petLocationViewModel.petLocationPetSalonData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationPetSalonData.value!!, requireContext())
            } }
            "박물관" -> { petLocationViewModel.petLocationMuseumData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationMuseumData.value!!, requireContext())
            } }
            "반려동물용품" -> { petLocationViewModel.petLocationToolsData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationToolsData.value!!, requireContext())
            } }
            "식당" -> { petLocationViewModel.petLocationRestaurantData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationRestaurantData.value!!, requireContext())
            } }
            "여행지" -> { petLocationViewModel.petLocationTripData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationTripData.value!!, requireContext())
            } }
            "위탁관리" -> { petLocationViewModel.petLocationManagementData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationManagementData.value!!, requireContext())
            } }
            "펜션" -> { petLocationViewModel.petLocationSwimmingData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationSwimmingData.value!!, requireContext())
            } }
            "호텔" -> { petLocationViewModel.petLocationHotelData.observe(requireActivity()) {
                rcLocationListView.adapter = LocationDataListAdapter(petLocationViewModel.petLocationHotelData.value!!, requireContext())
            } }
        }

        rcLocationListView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

}