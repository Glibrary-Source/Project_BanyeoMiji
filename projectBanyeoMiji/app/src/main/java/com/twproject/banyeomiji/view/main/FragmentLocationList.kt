package com.twproject.banyeomiji.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding
import com.twproject.banyeomiji.view.main.datamodel.PetLocationData
import com.twproject.banyeomiji.view.main.util.LocationListViewManager
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel

class FragmentLocationList : Fragment() {

    private lateinit var binding: FragmentLocationListBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var mContext: Context
    private val locationListViewManager = LocationListViewManager()
    private val categoryData by navArgs<FragmentLocationListArgs>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel =
            ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationListBinding.inflate(inflater)

        val rcLocationListView = binding.rcLocationList
        val activity = requireActivity()
        rcLocationListView.setHasFixedSize(true)

        locationListViewManager.setLocationDataList(
            categoryData.CategoryName,
            petLocationViewModel,
            activity,
            mContext,
            rcLocationListView,
            binding
        )

        rcLocationListView.layoutManager = LinearLayoutManager(requireContext())
        setRcPosition(rcLocationListView)

        return binding.root
    }

    private fun setRcPosition(rcLocationListView: RecyclerView) {
        try{
            if (petLocationViewModel.checkDirectionStoreMap.value!!) {
                val data: MutableList<PetLocationData> = when (categoryData.CategoryName) {
                    "문예회관" -> petLocationViewModel.petLocationGateData.value!!
                    "카페" -> petLocationViewModel.petLocationCafeData.value!!
                    "미술관" -> petLocationViewModel.petLocationArtGalleryData.value!!
                    "미용" -> petLocationViewModel.petLocationPetSalonData.value!!
                    "박물관" -> petLocationViewModel.petLocationMuseumData.value!!
                    "반려동물용품" -> petLocationViewModel.petLocationToolsData.value!!
                    "식당" -> petLocationViewModel.petLocationRestaurantData.value!!
                    "여행지" -> petLocationViewModel.petLocationTripData.value!!
                    "위탁관리" -> petLocationViewModel.petLocationManagementData.value!!
                    "펜션" -> petLocationViewModel.petLocationSwimmingData.value!!
                    else -> { mutableListOf() }
                }
                for((index, document) in data.withIndex()) {
                    if (document.FCLTY_NM == petLocationViewModel.selectPosition.value) {
                        rcLocationListView.scrollToPosition(index)
                        petLocationViewModel.setCheckDirection(false)
                    }
                }
            }
        } catch (_: Exception) {}
    }

}