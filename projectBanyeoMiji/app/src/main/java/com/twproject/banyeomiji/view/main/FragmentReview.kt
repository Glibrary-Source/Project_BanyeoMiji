package com.twproject.banyeomiji.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentReviewBinding
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.view.main.adapter.ReviewListAdapter
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FragmentReview : Fragment() {

    private lateinit var binding: FragmentReviewBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var mContext: Context
    private lateinit var activity: FragmentActivity
    private lateinit var reviewListData : Map<String, Any>
    private val reviewData by navArgs<FragmentReviewArgs>()
    val db = Firebase.firestore
    private var uid = ""

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            activity.findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility =
                View.VISIBLE
            val action =
                FragmentReviewDirections.actionFragmentReviewToFragmentLocationList(reviewData.TempCategoryName)
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

        petLocationViewModel = ViewModelProvider(activity)[PetLocationViewModel::class.java]
        activity.onBackPressedDispatcher.addCallback(this, callback)
        petLocationViewModel.getAllLocationData()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater)

        binding.btnExtendEditReview.bringToFront()
        binding.btnExtendEditReview.setOnClickListener {

            uid = GoogleObjectAuth.getFirebaseAuth().currentUser?.uid.toString()

            if( uid == "null") {
                Toast.makeText(mContext, "리뷰를 남기려면 로그인을 해주세요", Toast.LENGTH_SHORT).show()
            } else {
                reviewEditExtendController()
                binding.textReviewGone.visibility = View.GONE
            }

        }

        binding.btnReviewUpload.setOnClickListener {

            uid = GoogleObjectAuth.getFirebaseAuth().currentUser?.uid.toString()

            if( reviewListData.keys.toList().contains(uid) ) {
                Toast.makeText(mContext, "이미 리뷰를 작성하셨습니다", Toast.LENGTH_SHORT).show()
            } else if (uid == "null") {
                Toast.makeText(mContext, "리뷰를 남기려면 로그인을 해주세요", Toast.LENGTH_SHORT).show()
            } else {

                val reviewTitle = binding.editReviewTitle.text.toString()
                val reviewMain = binding.editReview.text.toString()
                val countLine = reviewMain.count{ it == '\n' }

                db.collection("user_db").document(uid)
                    .get()
                    .addOnSuccessListener {
                        val reviewItem =
                            mapOf("USER_REVIEW" to
                                    mapOf( uid to listOf(reviewTitle, reviewMain, it.data!!["nickname"]) )
                            )
                        if(reviewTitle == "" || reviewMain == "") {
                            Toast.makeText(mContext, "내용을 작성해주세요", Toast.LENGTH_SHORT).show()
                        } else if(countLine > 15){
                            Toast.makeText(mContext, "15줄 이하로 작성해주세요", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            CoroutineScope(IO).launch{
                                db.collection("pet_location_data")
                                    .document(reviewData.DocId)
                                    .set(reviewItem, SetOptions.merge())
                                    .addOnSuccessListener {
                                        Toast.makeText(mContext, "리뷰가 작성되었습니다", Toast.LENGTH_SHORT).show()
                                        reviewEditExtendController()
                                    }
                            }

                        }
                    }
            }
        }

        val reviewRecycler = binding.recyclerReviewList

        for(doc in petLocationViewModel.petAllLiveDataList.value!!) {
            if(doc.DOC_ID == reviewData.DocId) {
                reviewListData = doc.USER_REVIEW
                if(reviewListData.isEmpty()) {
                    binding.textReviewGone.visibility = View.VISIBLE
                }
            }
        }

        CoroutineScope(IO).launch {
            db.collection("pet_location_data")
                .document(reviewData.DocId)
                .addSnapshotListener { snapshot, e ->
                    if(e != null) {}
                    if(snapshot != null && snapshot.exists()) {
                        try{
                            val test = snapshot.data!!["USER_REVIEW"] as Map<String, Any>
                            reviewListData = test
                            reviewRecycler.adapter = ReviewListAdapter(test)
                            binding.textReviewGone.visibility = View.GONE
                        } catch (_: Exception) {

                        }
                    }
                }
        }

        return binding.root
    }

    private fun reviewEditExtendController() {
        binding.linearReviewEdit.visibility =
            if (binding.linearReviewEdit.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
}