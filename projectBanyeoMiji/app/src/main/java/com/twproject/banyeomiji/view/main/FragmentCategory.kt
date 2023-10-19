package com.twproject.banyeomiji.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentCategoryBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import com.twproject.banyeomiji.view.main.adapter.CategoryListAdapter
import com.twproject.banyeomiji.view.main.data.CategoryData
import com.twproject.banyeomiji.vbutility.BackPressCallBackManager
import com.twproject.banyeomiji.view.main.util.CategoryLocationDataManager
import com.twproject.banyeomiji.view.main.util.CategorySpinnerManager
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel

class FragmentCategory : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var userManager: UserSelectManager
    private lateinit var spinner: Spinner
    private lateinit var mContext: Context
    private lateinit var activity: FragmentActivity
    private lateinit var callback: OnBackPressedCallback
    private lateinit var categorySpinnerManager: CategorySpinnerManager
    private val categoryLocationDataManager = CategoryLocationDataManager()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = context as MainActivity

        callback = BackPressCallBackManager.setBackPressCallBack(activity, mContext)
        activity.onBackPressedDispatcher.addCallback(this, callback)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel = ViewModelProvider(activity)[PetLocationViewModel::class.java]
        userManager = UserSelectManager(mContext.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater)

        val items = resources.getStringArray(R.array.location_array)
        spinner = binding.spinnerCategoryLocationSelect

        categorySpinnerManager = CategorySpinnerManager(spinner, userManager)

        spinner.adapter = ArrayAdapter(mContext, R.layout.item_fragment_category_list_spinner, items)

        categorySpinnerManager.setStartSpinnerPosition()
        categorySpinnerManager.setSpinnerSelect(items, mContext, petLocationViewModel, categoryLocationDataManager)

        val rcCategoryView = binding.rcCategoryList
        rcCategoryView.setHasFixedSize(true)
        rcCategoryView.adapter = CategoryListAdapter(CategoryData().getCategoryList())
        rcCategoryView.layoutManager = GridLayoutManager(mContext, 2)

        return binding.root
    }

}



