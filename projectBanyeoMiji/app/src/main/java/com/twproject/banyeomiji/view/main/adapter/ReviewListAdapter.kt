package com.twproject.banyeomiji.view.main.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ReviewListAdapter(
    private val reviewDataList: Map<String, Any>,
    private val context: Context,
    private val db: FirebaseFirestore
): RecyclerView.Adapter<ReviewListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemDeclaration: ImageView = view.findViewById(R.id.btn_user_declaration)
        val itemTitle: TextView = view.findViewById(R.id.text_review_item_title)
        val itemMain: TextView = view.findViewById(R.id.text_review_item_main)
        val itemNickName: TextView = view.findViewById(R.id.text_review_item_nickname)
        val itemTime: TextView = view.findViewById(R.id.text_review_item_time)
        val itemRatingBar: RatingBar = view.findViewById(R.id.rating_review_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
         val adapterLayout = LayoutInflater.from(parent.context)
             .inflate(R.layout.item_fragment_review, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemStringArray = reviewDataList.values.toList()[position]
        val reviewUserUid = reviewDataList.keys.toList()[position]
        val castItem = itemStringArray as Map<*, *>
        val castRate = castItem["review_score"].toString().toFloat()

        holder.itemTitle.text = castItem["review_title"].toString()
        holder.itemMain.text = castItem["review_main"].toString()
        holder.itemNickName.text = "작성자: ${ castItem["review_nickname"].toString() }"
        holder.itemTime.text = castItem["review_time"].toString()
        holder.itemRatingBar.rating = castRate
        holder.itemDeclaration.setOnClickListener {
            ButtonAnimation().startAnimation(it)
            val builder = AlertDialog.Builder(context)
            val declarationDialog =
                builder.setTitle("리뷰 신고 알림")
                    .setMessage("부적절한 리뷰로 신고 하시겠습니까?")
                    .setPositiveButton("신고") { _, _ ->
                        reviewDeclaration(
                            reviewUserUid,
                            castItem["review_title"].toString(),
                            castItem["review_main"].toString()
                        )
                    }
                    .setNegativeButton("취소") { _, _ ->}
                    .setCancelable(false)
                    .create()
            declarationDialog.show()
            Toast.makeText(context, "부적절한 리뷰로 신고되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return reviewDataList.size
    }

    private fun reviewDeclaration(uid: String, reviewTitle: String, reviewMain: String) {
        CoroutineScope(IO).launch{
            val data = mapOf(
                "review_title" to reviewTitle,
                "review_main" to reviewMain
            )
            db.collection("user_declaration_db").document(uid)
                .set(data)
        }
    }

}