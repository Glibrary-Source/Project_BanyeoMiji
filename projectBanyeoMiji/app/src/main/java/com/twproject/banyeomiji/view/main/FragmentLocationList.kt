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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding
import com.twproject.banyeomiji.view.main.datamodel.PetLocationData
import com.twproject.banyeomiji.view.main.util.LocationListViewManager
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class FragmentLocationList : Fragment() {

    private lateinit var binding: FragmentLocationListBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var mContext: Context
    private lateinit var activity: MainActivity
    private val locationListViewManager = LocationListViewManager()
    private val categoryData by navArgs<FragmentLocationListArgs>()
    private var mInterstitialAd: InterstitialAd? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel =
            ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]
        admobControl()
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
            binding,
            categoryData.CategoryName
        )

        rcLocationListView.layoutManager = LinearLayoutManager(requireContext())
        setRcPosition(rcLocationListView)

        return binding.root
    }

    private fun setRcPosition(rcLocationListView: RecyclerView) {
        try {
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
                    else -> {
                        mutableListOf()
                    }
                }
                for ((index, document) in data.withIndex()) {
                    if (document.FCLTY_NM == petLocationViewModel.selectPosition.value) {
                        rcLocationListView.scrollToPosition(index)
                        petLocationViewModel.setCheckDirection(false)
                    }
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun admobControl() {
        MyGlobals.instance!!.fullAdCount ++
        if( MyGlobals.instance!!.fullAdCount % 10 == 0 ) {
            CoroutineScope(Main).launch{
                MobileAds.initialize(mContext) { loadAds() }
            }
        }
    }

    private fun loadAds() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            mContext,
//            "ca-app-pub-6758764449876389/5682667595",
            "ca-app-pub-6758764449876389/5682667595",
            adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {}

                            override fun onAdFailedToShowFullScreenContent(p0: AdError) {}

                            override fun onAdShowedFullScreenContent() {
                                mInterstitialAd = null
                            }
                        }

                    mInterstitialAd?.show(activity)
                }
            })
    }

}