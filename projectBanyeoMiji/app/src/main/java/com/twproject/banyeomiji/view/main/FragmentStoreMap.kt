package com.twproject.banyeomiji.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentStoreMapBinding
import com.twproject.banyeomiji.view.main.data.LocationLatLng
import com.twproject.banyeomiji.vbutility.BackPressCallBackManager
import com.twproject.banyeomiji.view.main.util.PermissionManager
import com.twproject.banyeomiji.view.main.util.StoreMapSetEditor
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FragmentStoreMap : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentStoreMapBinding
    private lateinit var mContext: Context
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverFragmentMap: NaverMap
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var storeMapSetEditor: StoreMapSetEditor
    private lateinit var callback: OnBackPressedCallback
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var navController: NavController

    private val requestMultiplePermission = getPermissionLauncher()
    private val permissionManager = PermissionManager()
    private val locationLatLng = LocationLatLng()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
        fragmentActivity = requireActivity()

        callback = BackPressCallBackManager.setBackPressCallBack(fragmentActivity, mContext)
        fragmentActivity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(fragmentActivity, LOCATION_PERMISSION_REQUEST_CODE)
        petLocationViewModel = ViewModelProvider(fragmentActivity)[PetLocationViewModel::class.java]
        petLocationViewModel.getAllLocationData()
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreMapBinding.inflate(inflater)


        if(MyGlobals.instance!!.firstExplain) {
            Toast.makeText(mContext, "목록에서 보고싶은 카테고리를 체크해주세요", Toast.LENGTH_SHORT).show()
            MyGlobals.instance!!.firstExplain = false
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
        mapFragment!!.getMapAsync(this)

        binding.filterMenu.layout01.setOnClickListener(getFilterMenuOnClickListener())

        return binding.root
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverFragmentMap = naverMap
        storeMapSetEditor = StoreMapSetEditor(naverFragmentMap)
        storeMapSetEditor.setLocationSource(locationSource)
        storeMapSetEditor.uiSetting()
        storeMapSetEditor.setMapExtent()
        storeMapSetEditor.setMaxMinZoom()

        if (petLocationViewModel.permissionCheck.value!!) {
            requestMultiplePermission.launch(permissionManager.getPermissionList())
            petLocationViewModel.setPermissionCheck(false)
        }

        storeMapSetEditor.locationChangeMoveCamera(petLocationViewModel, locationLatLng)

        val markerList = mutableMapOf<Marker, String>()

        CoroutineScope(IO).launch {
            for (document in petLocationViewModel.petAllLiveDataList.value!!) {

                val overlayIcon = storeMapSetEditor.getMarkerIcon(document.CTGRY_THREE_NM)

                val infoWindow = InfoWindow().apply {
                    this.onClickListener = storeMapSetEditor.setMarkerInfoWindowOnClickListener(
                        document, navController, petLocationViewModel
                    )
                    this.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                        override fun getText(p0: InfoWindow): CharSequence {
                            return document.FCLTY_NM
                        }
                    }
                }

                val marker = Marker().apply {
                    this.position = LatLng(document.LC_LA, document.LC_LO)
                    this.icon = overlayIcon
                    this.onClickListener = getOverlayListener(infoWindow)
                    this.width = 100
                    this.height = 100
                }

                markerList[marker] = document.CTGRY_THREE_NM
            }

            withContext(Main) {
                updateMarkerVisibility(markerList)
            }
        }

        //지도 클릭시 실행
        naverFragmentMap.setOnMapClickListener { _, _ ->
            for ((marker) in markerList) {
                if (marker.infoWindow != null) {
                    marker.infoWindow!!.close()
                }
            }
            binding.filterMenu.layoutDetail01.visibility = View.GONE
        }

        setCheckBoxListener(getCheckBoxListener(markerList))
    }

    private fun getFilterMenuOnClickListener() : OnClickListener {
        return OnClickListener {
            if (binding.filterMenu.layoutDetail01.visibility == View.VISIBLE) {
                binding.filterMenu.layoutDetail01.visibility = View.GONE
                binding.filterMenu.layoutBtn01.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            } else {
                binding.filterMenu.layoutDetail01.visibility = View.VISIBLE
                binding.filterMenu.layoutBtn01.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            }
        }
    }



    private fun updateMarkerVisibility(markerList: MutableMap<Marker, String>) {
        for ((marker, category) in markerList) {
            marker.map = naverFragmentMap

            val checkBox = when (category) {
                "문예회관" -> binding.filterMenu.cbKoreaGate
                "카페" -> binding.filterMenu.cbKoreaCafe
                "미술관" -> binding.filterMenu.cbKoreaArtGallery
                "미용" -> binding.filterMenu.cbKoreaPetSalon
                "박물관" -> binding.filterMenu.cbKoreaMuseum
                "반려동물용품" -> binding.filterMenu.cbKoreaDogTools
                "식당" -> binding.filterMenu.cbKoreaRestaurant
                "여행지" -> binding.filterMenu.cbKoreaTrip
                "위탁관리" -> binding.filterMenu.cbKoreaManagement
                "펜션" -> binding.filterMenu.cbKoreaSwimmingPool
                else -> null
            }

            if (checkBox != null && !checkBox.isChecked) {
                marker.isVisible = false
            }
        }
    }

    private fun getPermissionLauncher(): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            result.forEach {
                if (!it.value) {
                    Toast.makeText(requireContext(), "위치 권한 허용 필요", Toast.LENGTH_SHORT).show()
                } else {
                    naverFragmentMap.locationTrackingMode = LocationTrackingMode.Follow
                }
            }
        }
    }

    private fun getCheckBoxListener( markerList : MutableMap<Marker, String> ) : CompoundButton.OnCheckedChangeListener {
        return CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                when(buttonView.id) {
                    R.id.cb_korea_gate -> { for((marker, category) in markerList){ if(category == "문예회관") marker.isVisible = true}}
                    R.id.cb_korea_cafe -> { for((marker, category) in markerList){ if(category == "카페") marker.isVisible = true}}
                    R.id.cb_korea_art_gallery -> { for((marker, category) in markerList){ if(category == "미술관") marker.isVisible = true}}
                    R.id.cb_korea_pet_salon -> { for((marker, category) in markerList){ if(category == "미용") marker.isVisible = true}}
                    R.id.cb_korea_museum -> { for((marker, category) in markerList){ if(category == "박물관") marker.isVisible = true}}
                    R.id.cb_korea_dog_tools -> { for((marker, category) in markerList){ if(category == "반려동물용품") marker.isVisible = true}}
                    R.id.cb_korea_restaurant -> { for((marker, category) in markerList){ if(category == "식당") marker.isVisible = true}}
                    R.id.cb_korea_trip -> { for((marker, category) in markerList){ if(category == "여행지") marker.isVisible = true}}
                    R.id.cb_korea_management -> { for((marker, category) in markerList){ if(category == "위탁관리") marker.isVisible = true}}
                    R.id.cb_korea_swimming_pool -> { for((marker, category) in markerList){ if(category == "펜션") marker.isVisible = true}}
                }
            } else {
                when(buttonView.id) {
                    R.id.cb_korea_gate -> { for((marker, category) in markerList){ if(category == "문예회관") marker.isVisible = false}}
                    R.id.cb_korea_cafe -> { for((marker, category) in markerList){ if(category == "카페") marker.isVisible = false}}
                    R.id.cb_korea_art_gallery -> { for((marker, category) in markerList){ if(category == "미술관") marker.isVisible = false}}
                    R.id.cb_korea_pet_salon -> { for((marker, category) in markerList){ if(category == "미용") marker.isVisible = false}}
                    R.id.cb_korea_museum -> { for((marker, category) in markerList){ if(category == "박물관") marker.isVisible = false}}
                    R.id.cb_korea_dog_tools -> { for((marker, category) in markerList){ if(category == "반려동물용품") marker.isVisible = false}}
                    R.id.cb_korea_restaurant -> { for((marker, category) in markerList){ if(category == "식당") marker.isVisible = false}}
                    R.id.cb_korea_trip -> { for((marker, category) in markerList){ if(category == "여행지") marker.isVisible = false}}
                    R.id.cb_korea_management -> { for((marker, category) in markerList){ if(category == "위탁관리") marker.isVisible = false}}
                    R.id.cb_korea_swimming_pool -> { for((marker, category) in markerList){ if(category == "펜션") marker.isVisible = false}}
                }
            }
        }
    }

    private fun setCheckBoxListener(listener :  CompoundButton.OnCheckedChangeListener) {
        val checkBoxIds = arrayOf(
            R.id.cb_korea_gate,
            R.id.cb_korea_cafe,
            R.id.cb_korea_art_gallery,
            R.id.cb_korea_pet_salon,
            R.id.cb_korea_museum,
            R.id.cb_korea_dog_tools,
            R.id.cb_korea_restaurant,
            R.id.cb_korea_trip,
            R.id.cb_korea_management,
            R.id.cb_korea_swimming_pool
        )

        for (checkBoxId in checkBoxIds) {
            val checkBox = binding.root.findViewById<CheckBox>(checkBoxId)
            checkBox.setOnCheckedChangeListener(listener)
        }
    }

    private fun getOverlayListener(infoWindow: InfoWindow) : Overlay.OnClickListener {
        return Overlay.OnClickListener { overlay ->
            val marker = overlay as Marker
            if (marker.infoWindow == null) {
                infoWindow.open(marker)
                binding.filterMenu.layoutDetail01.visibility = View.GONE
            } else {
                infoWindow.close()
            }
            true
        }
    }

}