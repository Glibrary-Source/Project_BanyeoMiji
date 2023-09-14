package com.twproject.banyeomiji.view

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentStoreMapBinding
import com.twproject.banyeomiji.view.util.StoreMapSetEditor
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel
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

    private val requestMultiplePermission = getPermissionLauncher()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)
        petLocationViewModel =
            ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]
        petLocationViewModel.getAllLocationData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStoreMapBinding.inflate(inflater)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
        mapFragment!!.getMapAsync(this)

        binding.filterMenu.layout01.setOnClickListener {
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

        return binding.root
    }

    override fun onMapReady(naverMap: NaverMap) {
        naverFragmentMap = naverMap
        storeMapSetEditor = StoreMapSetEditor(naverFragmentMap)

        if (petLocationViewModel.permissionCheck.value!!) {
            requestMultiplePermission.launch(getPermissionList())
            petLocationViewModel.setPermissionCheck(false)
        }

        storeMapSetEditor.setLocationSource(locationSource)
        storeMapSetEditor.uiSetting()
//        storeMapSetEditor.setMaxMinZoom()

        val markerList = mutableListOf<Marker>()

        CoroutineScope(IO).launch {
            for (document in petLocationViewModel.petAllLiveDataList.value!!) {
                val overlayIcon = getMarkerIcon(document.CTGRY_THREE_NM)

                val infoWindow = InfoWindow()
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                    override fun getText(p0: InfoWindow): CharSequence {
                        return document.FCLTY_NM
                    }
                }

                val listener = Overlay.OnClickListener { overlay ->
                    val marker = overlay as Marker

                    if (marker.infoWindow == null) {
                        infoWindow.open(marker)
                    } else {
                        infoWindow.close()
                    }
                    true
                }

                val marker = Marker()
                marker.position = LatLng(document.LC_LA, document.LC_LO)
                marker.icon = overlayIcon
                marker.width = 100
                marker.height = 100
                marker.onClickListener = listener

                markerList.add(marker)
            }

            withContext(Main) {
                for (marker in markerList) {
                    marker.map = naverFragmentMap
                }
            }

        }

        //지도 클릭시 실행
        naverFragmentMap.setOnMapClickListener { _, _ ->
            for (marker in markerList) {
                if (marker.infoWindow != null) {
                    marker.infoWindow!!.close()
                }
            }
        }

        // 지도 카메라 변화시 실행
//        naverFragmentMap.addOnCameraChangeListener { _, _ ->
//            for (marker in markerList) {
//                if(marker.infoWindow != null){ marker.infoWindow!!.close() }
//            }
//        }

//        binding.btnTestmap.setOnClickListener {
//            for (marker in markerList) {
//                if(marker.infoWindow != null){ marker.infoWindow!!.close() }
//            }
//        }

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

    private fun getPermissionList(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    private fun getMarkerIcon(storeCategory: String): OverlayImage {
        return when (storeCategory) {
            "문예회관" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_gate)
            "카페" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_cafe)
            "미술관" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_art_gallery)
            "미용" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_petsalon)
            "박물관" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_museum)
            "반려동물용품" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_dog_tools)
            "식당" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_restaurant)
            "여행지" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_trip)
            "위탁관리" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_management)
            "펜션" -> OverlayImage.fromResource(R.drawable.img_category_item_korea_swimming_pool)
            else -> {
                OverlayImage.fromResource(R.drawable.img_category_item_korea_gate)
            }
        }
    }
}