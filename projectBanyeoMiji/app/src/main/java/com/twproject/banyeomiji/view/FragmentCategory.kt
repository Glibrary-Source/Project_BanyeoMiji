package com.twproject.banyeomiji.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.twproject.banyeomiji.databinding.FragmentCategoryBinding
import com.twproject.banyeomiji.view.adapter.CategoryListAdapter
import com.twproject.banyeomiji.view.data.CategoryData
import com.twproject.banyeomiji.view.util.NavigationOptionsManager
import com.twproject.banyeomiji.view.viewmodel.PetLocationViewModel

class FragmentCategory : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var petLocationViewModel: PetLocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        petLocationViewModel = ViewModelProvider(requireActivity())[PetLocationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater)

        val navOptions = NavigationOptionsManager(findNavController())
        val recyclerView = binding.rcCategoryList
        recyclerView.adapter = CategoryListAdapter(CategoryData().getCategoryList(), navOptions.setBottomTransformOption())
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        return binding.root
    }
}