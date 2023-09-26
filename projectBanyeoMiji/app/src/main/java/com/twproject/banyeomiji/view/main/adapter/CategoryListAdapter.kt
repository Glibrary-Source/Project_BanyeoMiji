package com.twproject.banyeomiji.view.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.main.FragmentCategoryDirections
import com.twproject.banyeomiji.view.main.util.AdapterStringManager
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import com.twproject.banyeomiji.vbutility.onThrottleClick
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryListAdapter(
    private val dataList: MutableMap<String, Int>,
) : RecyclerView.Adapter<CategoryListAdapter.ItemViewHolder>() {

    private val stringManager = AdapterStringManager()

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryButton: TextView = view.findViewById(R.id.text_category_item_title)
        val linearItem: LinearLayout = view.findViewById(R.id.linear_item)
        val categoryImg: ImageView = view.findViewById(R.id.img_category_item)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment_category_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemName = dataList.keys.toList()[position]
        val itemImg = dataList.values.toList()[position]
        holder.categoryButton.text = stringManager.checkHotel(itemName)
        holder.categoryImg.setImageResource(itemImg)
        holder.linearItem.onThrottleClick {
            ButtonAnimation().startAnimation(it)
            CoroutineScope(Main).launch {
                val action =
                    FragmentCategoryDirections.actionFragmentCategoryToFragmentLocationList(itemName)
                delay(300)
                holder.linearItem.findNavController().navigate(action)
            }
        }
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
}