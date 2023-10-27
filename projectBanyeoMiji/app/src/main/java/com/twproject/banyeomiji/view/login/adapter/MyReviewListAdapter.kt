package com.twproject.banyeomiji.view.login.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.vbutility.ButtonAnimation

class MyReviewListAdapter(
    private val reviewDataList: Map<String, Any>,
    private val context: Context,
    private val db: FirebaseFirestore,
    private val currentUid: String
): RecyclerView.Adapter<MyReviewListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemEraseReview: ImageView = view.findViewById(R.id.btn_my_review_erase)
        val itemMyStoreName: TextView = view.findViewById(R.id.text_my_review_store_name)
        val itemMyTitle: TextView = view.findViewById(R.id.text_my_review_item_title)
        val itemMyMain: TextView = view.findViewById(R.id.text_my_review_item_main)
        val itemMyNickName: TextView = view.findViewById(R.id.text_my_review_item_nickname)
        val itemMyTime: TextView = view.findViewById(R.id.text_my_review_item_time)
        val itemMyRatingBar: RatingBar = view.findViewById(R.id.rating_my_review_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment_my_review, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemReviewArray = reviewDataList.values.toList()[position]
        val itemDocArray = reviewDataList.keys.toList()[position]
        val castItem = itemReviewArray as Map<*, *>
        val castRate = castItem["review_score"].toString().toFloat()
        holder.itemMyStoreName.text = castItem["review_store_name"].toString()
        holder.itemMyTitle.text = castItem["review_title"].toString()
        holder.itemMyMain.text = castItem["review_main"].toString()
        holder.itemMyNickName.text = "작성자: ${castItem["review_nickname"].toString()}"
        holder.itemMyTime.text = castItem["review_time"].toString()
        holder.itemMyRatingBar.rating = castRate
        holder.itemEraseReview.setOnClickListener {
            ButtonAnimation().startAnimation(it)
            val builder = AlertDialog.Builder(context)
            val eraseDialog =
                builder.setTitle("리뷰 삭제 알림")
                    .setMessage("정말 리뷰를 삭제 하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        deleteReview(itemDocArray)
                    }
                    .setNegativeButton("취소") { _, _ ->}
                    .setCancelable(false)
                    .create()
            eraseDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return reviewDataList.size
    }

    private fun deleteReview(docId: String) {
        val userDB = db.collection("user_db").document(currentUid)
        val userUpdates = hashMapOf<String, Any>(
            "USER_REVIEW.$docId" to FieldValue.delete()
        )

        userDB.update(userUpdates).addOnSuccessListener{}

        val docDB = db.collection( "pet_location_data").document(docId)
        val docUpdates = hashMapOf<String, Any>(
            "USER_REVIEW.$currentUid" to FieldValue.delete()
        )

        docDB.update(docUpdates).addOnSuccessListener {}


    }

}