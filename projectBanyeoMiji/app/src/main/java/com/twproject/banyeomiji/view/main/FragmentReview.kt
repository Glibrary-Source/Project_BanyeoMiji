package com.twproject.banyeomiji.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentReviewBinding
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.view.main.adapter.ReviewListAdapter
import com.twproject.banyeomiji.view.main.viewmodel.PetLocationViewModel
import com.vane.badwordfiltering.BadWordFiltering
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class FragmentReview : Fragment() {

    private lateinit var binding: FragmentReviewBinding
    private lateinit var petLocationViewModel: PetLocationViewModel
    private lateinit var mContext: Context
    private lateinit var activity: FragmentActivity
//    private lateinit var reviewListData: Map<String, Any>
    private var reviewListData: Map<String, Any> = mapOf()
    private val reviewData by navArgs<FragmentReviewArgs>()
    val db = Firebase.firestore
    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private var loginState = "init"
    private var currentUid = "default"
    private val badWordFilter = BadWordFiltering()

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
        setLoginStateAndUid()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater)

        binding.btnExtendEditReview.bringToFront()
        binding.btnExtendEditReview.setOnClickListener {

            setLoginStateAndUid()

            if (currentUid == "null") {
                Toast.makeText(mContext, "리뷰를 남기려면 로그인을 해주세요", Toast.LENGTH_SHORT).show()
            } else {
                reviewEditExtendControl()
                reviewGoneVisibleControl()
            }
        }

        var reviewScore = 5F
        binding.ratingReview.setOnRatingBarChangeListener { _, rating, _ ->
            reviewScore = rating
        }

        binding.btnReviewUpload.setOnClickListener {

            setLoginStateAndUid()

            if (currentUid == "null") {
                Toast.makeText(mContext, "로그인을 해주세요", Toast.LENGTH_SHORT).show()
            } else if (reviewListData.keys.toList().contains(currentUid)) {
                Toast.makeText(mContext, "이미 리뷰를 작성하셨습니다", Toast.LENGTH_SHORT).show()
            } else {
                val reviewTitle = binding.editReviewTitle.text.toString()
                val reviewMain = binding.editReview.text.toString()
                val countLine = reviewMain.count { it == '\n' }
                val currentTime = getCurrentTime()

                db.collection("user_db").document(currentUid)
                    .get()
                    .addOnSuccessListener {
                        val data = it.data
                        if(data == null) {
                            Toast.makeText(mContext, "로그인을 해주세요", Toast.LENGTH_SHORT).show()
                        } else {
                            val reviewItem = setUserReviewItem(
                                reviewTitle,
                                reviewMain,
                                it.data!!["nickname"],
                                currentTime,
                                reviewScore
                            )
                            val myProfileReviewItem = setUserReviewMyPageItem(
                                reviewTitle,
                                reviewMain,
                                it.data!!["nickname"],
                                currentTime,
                                reviewScore,
                                reviewData.DocId,
                                reviewData.StoreName
                            )

                            if (reviewTitle == "" || reviewMain == "") {
                                Toast.makeText(mContext, "내용을 작성해주세요", Toast.LENGTH_SHORT).show()
                            } else if (countLine > 20) {
                                Toast.makeText(mContext, "20줄 이하로 작성해주세요", Toast.LENGTH_SHORT).show()
                            } else if(badWordFilter.check(reviewTitle) || badWordFilter.check(reviewMain)){
                                Toast.makeText(mContext, "비속어를 제거해주세요", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                CoroutineScope(IO).launch {

                                    db.collection("user_review_db")
                                        .document(reviewData.DocId)
                                        .set(reviewItem, SetOptions.merge())
                                        .addOnSuccessListener {
                                            Toast.makeText(mContext, "리뷰가 작성되었습니다", Toast.LENGTH_SHORT)
                                                .show()
                                            reviewEditExtendControl()
                                        }

                                    db.collection("user_db")
                                        .document(currentUid)
                                        .set(myProfileReviewItem, SetOptions.merge())
                                }

                            }
                        }
                    }
            }
        }

        val reviewRecycler = binding.recyclerReviewList

        CoroutineScope(IO).launch {
            db.collection("user_review_db")
                .document(reviewData.DocId)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && snapshot.exists()) {
                        try {
                            val item = snapshot.data?.get("USER_REVIEW")
                            if (item == null) {
                                reviewListData = mapOf()
                                reviewRecycler.adapter = ReviewListAdapter(reviewListData, mContext, db)
                                reviewGoneVisibleControl()
                            } else {
                                val test = snapshot.data!!["USER_REVIEW"] as Map<*, *>
                                val addMap = mutableMapOf<String, Any>()
                                for ((key, value) in test) {
                                    addMap[key.toString()] = value as Any
                                }
                                reviewListData = addMap
                                reviewRecycler.adapter = ReviewListAdapter(reviewListData, mContext, db)
                                reviewGoneVisibleControl()
                            }
                        } catch (_: Exception) {}
                    }
                    else {
                        reviewGoneVisibleControl()
                    }
                }
        }

        return binding.root
    }

    override fun onResume() {
        setLoginStateAndUid()
        super.onResume()
    }

    private fun reviewEditExtendControl() {
        binding.linearReviewEdit.visibility =
            if (binding.linearReviewEdit.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun reviewGoneVisibleControl() {
        if (binding.textReviewGone.visibility == View.VISIBLE) {
            binding.textReviewGone.visibility = View.GONE
        } else if (binding.textReviewGone.visibility == View.GONE && reviewListData.isEmpty()) {
            binding.textReviewGone.visibility = View.VISIBLE
        }
    }

    private fun setLoginStateAndUid() {
        loginState = if (auth.currentUser != null && MyGlobals.instance!!.userDataCheck == 1) {
            "google"
        } else if (NaverIdLoginSDK.getState().name != "NEED_LOGIN" && NaverIdLoginSDK.getState().name != "NEED_INIT" && NaverIdLoginSDK.getState().name != "NEED_REFRESH_TOKEN") {
            "naver"
        } else {
            "null"
        }
        setStateUid()
    }

    private fun setStateUid() {
        when (loginState) {
            "google" -> {
                currentUid = auth.currentUser!!.uid
            }

            "naver" -> {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        currentUid = result.profile?.id.toString()
                    }

                    override fun onError(errorCode: Int, message: String) {}
                    override fun onFailure(httpStatus: Int, message: String) {}
                })
            }

            "null" -> currentUid = "null"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentTime(): String {
        val timeDate: String
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = LocalDate.now()
            val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val nowString = date.format(dtf)
            timeDate = nowString
            timeDate
        } else {
            val utilDate = Date()
            val formatType = SimpleDateFormat("yyyy-MM-dd")
            timeDate = formatType.format(utilDate)
            timeDate
        }
    }

    private fun setUserReviewItem(
        reviewTitle: String,
        reviewMain: String,
        nickname: Any?,
        currentTime: String,
        score: Float
    ): Map<String, Map<String, Map<String, Any?>>> {
        return mapOf(
            "USER_REVIEW" to
                    mapOf(
                        currentUid to mapOf(
                            "review_title" to reviewTitle,
                            "review_main" to reviewMain,
                            "review_nickname" to nickname,
                            "review_time" to currentTime,
                            "review_score" to score
                        )
                    )
        )
    }

    private fun setUserReviewMyPageItem(
        reviewTitle: String,
        reviewMain: String,
        nickname: Any?,
        currentTime: String,
        score: Float,
        docId: String,
        storeName: String
    ): Map<String, Map<String, Map<String, Any?>>> {
        return mapOf(
            "USER_REVIEW" to
                    mapOf(
                        docId to mapOf(
                            "review_title" to reviewTitle,
                            "review_main" to reviewMain,
                            "review_nickname" to nickname,
                            "review_time" to currentTime,
                            "review_score" to score,
                            "review_store_name" to storeName
                        )
                    )
        )
    }

}