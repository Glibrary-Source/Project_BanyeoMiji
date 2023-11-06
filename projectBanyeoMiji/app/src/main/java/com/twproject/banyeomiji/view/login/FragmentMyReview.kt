package com.twproject.banyeomiji.view.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.databinding.FragmentMyReviewBinding
import com.twproject.banyeomiji.view.login.adapter.MyReviewListAdapter
import com.twproject.banyeomiji.view.main.FragmentLocationListArgs
import com.twproject.banyeomiji.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentMyReview : Fragment() {

    private lateinit var mContext: Context
    private lateinit var activity: MainActivity
    private lateinit var binding: FragmentMyReviewBinding
    private val myReviewData by navArgs<FragmentMyReviewArgs>()

    private val db = Firebase.firestore

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = mContext as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyReviewBinding.inflate(inflater)

        val currentUid = myReviewData.currentUid
        binding.recyclerViewMyReview.layoutManager = LinearLayoutManager(mContext)

        when(currentUid){
            "wait" -> {
                Toast.makeText(mContext, "로그인을 다시시도해주세요", Toast.LENGTH_SHORT).show()
            }
            else -> {
                CoroutineScope(Dispatchers.IO).launch {
                    db.collection("user_db")
                        .document(currentUid)
                        .addSnapshotListener { snapshot, _ ->
                            if (snapshot != null && snapshot.exists()) {
                                try {
                                    val item = snapshot.data?.get("USER_REVIEW")
                                    val emptyMap = mutableMapOf<String, Any>()
                                    if (item == null) {
                                        emptyReviewControl(currentUid, emptyMap)
                                    } else {
                                        val test = snapshot.data!!["USER_REVIEW"] as Map<*, *>
                                        if (test.isEmpty()) {
                                            emptyReviewControl(currentUid, emptyMap)
                                        } else {
                                            val addMap = mutableMapOf<String, Any>()
                                            for ((key, value) in test) {
                                                addMap[key.toString()] = value as Any
                                            }
                                            binding.recyclerViewMyReview.adapter =
                                                MyReviewListAdapter(addMap, mContext, db, currentUid)
                                            binding.textReviewGone.visibility = View.GONE
                                        }
                                    }
                                } catch (_: Exception) {
                                }
                            }
                        }
                }
            }
        }

        return binding.root
    }

    private fun emptyReviewControl(currentUid: String, emptyMap:MutableMap<String, Any> ) {
        binding.textReviewGone.visibility = View.VISIBLE
        binding.recyclerViewMyReview.adapter =
            MyReviewListAdapter(emptyMap, mContext, db, currentUid)
    }

}