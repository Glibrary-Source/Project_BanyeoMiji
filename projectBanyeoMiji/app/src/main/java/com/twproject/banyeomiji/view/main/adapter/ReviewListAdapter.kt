package com.twproject.banyeomiji.view.main.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.R

class ReviewListAdapter(
    private val reviewDataList: Map<String, Any>
): RecyclerView.Adapter<ReviewListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        val castItem = itemStringArray as Map<*, *>
        val castRate = castItem["review_score"].toString().toFloat()

        holder.itemTitle.text = castItem["review_title"].toString()
        holder.itemMain.text = castItem["review_main"].toString()
        holder.itemNickName.text = "작성자: ${castItem["review_nickname"].toString()}"
        holder.itemTime.text = castItem["review_time"].toString()
        holder.itemRatingBar.rating = castRate
    }

    override fun getItemCount(): Int {
        return reviewDataList.size
    }

}