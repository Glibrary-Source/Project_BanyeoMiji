package com.twproject.banyeomiji.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.vbutility.BackPressCallBackManager

class FragmentReview : Fragment() {

    private lateinit var mContext: Context
    private lateinit var activity: FragmentActivity
    private val reviewData by navArgs<FragmentReviewArgs>()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity.findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility = View.VISIBLE
            val action = FragmentReviewDirections.actionFragmentReviewToFragmentLocationList(reviewData.TempCategoryName)
            findNavController().navigate(action)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
        activity = requireActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false)
    }
}