package com.twproject.banyeomiji.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentCategoryBinding
import com.twproject.banyeomiji.view.adapter.CategoryListAdapter
import com.twproject.banyeomiji.view.data.CategoryData
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentCategory : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var userManager: UserSelectManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel =
            ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]

        userManager = UserSelectManager(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater)

        val items = resources.getStringArray(R.array.location_array)
        val mySpinnerAdapter =
            ArrayAdapter(requireContext(), R.layout.item_fragment_category_list_spinner, items)
        val spinner = binding.spinnerCategoryLocationSelect
        spinner.adapter = mySpinnerAdapter

        CoroutineScope(IO).launch {
            userManager.userSelectFlow.collect {
                if(it != null) {
                    withContext(Main){
                        spinner.setSelection(it)
                    }
                }
            }
        }
        userManager = UserSelectManager(requireContext().dataStore)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectLocationData(petLocationViewModel, items[position])
                CoroutineScope(IO).launch {
                    userManager.selectUser(position)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val rcCategoryView = binding.rcCategoryList
        rcCategoryView.setHasFixedSize(true)
        rcCategoryView.adapter = CategoryListAdapter(CategoryData().getCategoryList())
        rcCategoryView.layoutManager = GridLayoutManager(requireContext(), 2)

        return binding.root
    }
}

private fun selectLocationData(petLocationViewModel: PetLocationViewModel, location: String) {
    CoroutineScope(IO).launch {
        petLocationViewModel.getLocationData("문예회관", location)
        petLocationViewModel.getLocationData("카페", location)
        petLocationViewModel.getLocationData("미술관", location)
        petLocationViewModel.getLocationData("미용", location)
        petLocationViewModel.getLocationData("박물관", location)
        petLocationViewModel.getLocationData("반려동물용품", location)
        petLocationViewModel.getLocationData("식당", location)
        petLocationViewModel.getLocationData("여행지", location)
        petLocationViewModel.getLocationData("위탁관리", location)
        petLocationViewModel.getLocationData("펜션", location)
    }
}