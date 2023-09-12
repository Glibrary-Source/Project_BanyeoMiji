package com.twproject.banyeomiji.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding
import com.twproject.banyeomiji.view.adapter.LocationDataListAdapter
import com.twproject.banyeomiji.view.util.LocationListViewManager
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel

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

        locationListViewManager.setLocationDataList(
            categoryData.CategoryName,
            petLocationViewModel,
            activity,
            mContext,
            rcLocationListView
        )

        rcLocationListView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }
}