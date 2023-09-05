package com.twproject.banyeomiji.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding

class FragmentLocationList : Fragment() {

    private lateinit var binding: FragmentLocationListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationListBinding.inflate(inflater)


        return binding.root
    }

}