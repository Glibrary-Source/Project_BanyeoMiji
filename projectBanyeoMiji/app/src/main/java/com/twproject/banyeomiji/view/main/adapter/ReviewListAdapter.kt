package com.twproject.banyeomiji.view.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
         val adapterLayout = LayoutInflater.from(parent.context)
             .inflate(R.layout.item_fragment_review, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemStringArray = reviewDataList.values.toList()[position]
        val castItem = itemStringArray as List<*>
        holder.itemTitle.text = castItem[0].toString()
        holder.itemMain.text = castItem[1].toString()
        holder.itemNickName.text = "작성자: ${castItem[2].toString()}"
    }

    override fun getItemCount(): Int {
        return reviewDataList.size
    }

}