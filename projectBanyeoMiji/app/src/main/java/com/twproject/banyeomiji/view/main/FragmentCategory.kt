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
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentCategoryBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import com.twproject.banyeomiji.view.main.data.CategoryData
import com.twproject.banyeomiji.vbutility.BackPressCallBackManager
import com.twproject.banyeomiji.view.main.adapter.CategoryViewPagerAdapter
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

        val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin) // dimen 파일 안에 크기를 정의해두었다.
        val pagerWidth = resources.getDimensionPixelOffset(R.dimen.pageWidth) // dimen 파일이 없으면 생성해야함
        val screenWidth = resources.displayMetrics.widthPixels // 스마트폰의 너비 길이를 가져옴
        val offsetPx = screenWidth - pageMarginPx - pagerWidth

        val viewPager2 = binding.vpCategoryList
        viewPager2.setPageTransformer { page, position ->
            page.translationX = position * -offsetPx
        }
        viewPager2.offscreenPageLimit = 1
        viewPager2.adapter = CategoryViewPagerAdapter(CategoryData().getCategoryList())

        val dotIndicator = binding.dotIndicator
        dotIndicator.attachTo(viewPager2)

        MyGlobals.instance!!.navState = "category"

        return binding.root
    }

}



