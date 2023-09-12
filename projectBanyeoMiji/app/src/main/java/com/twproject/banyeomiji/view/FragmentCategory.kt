package com.twproject.banyeomiji.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentCategoryBinding
import com.twproject.banyeomiji.view.adapter.CategoryListAdapter
import com.twproject.banyeomiji.view.data.CategoryData
import com.twproject.banyeomiji.view.util.CategoryLocationDataManager
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
import kotlin.system.exitProcess

class FragmentCategory : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var userManager: UserSelectManager
    private lateinit var spinner: Spinner
    private lateinit var mContext: Context
    private val categoryLocationDataManager = CategoryLocationDataManager()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel = ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]
        userManager = UserSelectManager(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater)

        val items = resources.getStringArray(R.array.location_array)
        spinner = binding.spinnerCategoryLocationSelect
        spinner.adapter = ArrayAdapter(requireContext(), R.layout.item_fragment_category_list_spinner, items)

        setStartSpinnerPosition()
        setSpinnerSelect(items)

        val rcCategoryView = binding.rcCategoryList
        rcCategoryView.setHasFixedSize(true)
        rcCategoryView.adapter = CategoryListAdapter(CategoryData().getCategoryList())
        rcCategoryView.layoutManager = GridLayoutManager(requireContext(), 2)

        return binding.root
    }

    private fun setSpinnerSelect(items: Array<String>) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val loadD = LoadingDialog(mContext)
                setStartSpinnerPosition()
                if (petLocationViewModel.spinningPosition.value != position){
                    loadD.show()
                    categoryLocationDataManager.selectLocationData(
                        petLocationViewModel,
                        items[position],
                        loadD
                    )
                    CoroutineScope(IO).launch {
                        userManager.selectUser(position)
                    }
                }
                petLocationViewModel.setSpinningPosition(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setStartSpinnerPosition() {
        CoroutineScope(IO).launch {
            userManager.userSelectFlow.collect {
                if (it != null) {
                    withContext(Main) {
                        spinner.setSelection(it)
                    }
                }
            }
        }
    }

}



