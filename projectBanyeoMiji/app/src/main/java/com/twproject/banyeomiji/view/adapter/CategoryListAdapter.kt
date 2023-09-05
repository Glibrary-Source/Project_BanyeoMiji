package com.twproject.banyeomiji.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.FragmentCategoryDirections
import com.twproject.banyeomiji.view.util.CategoryButtonAnimation
import com.twproject.banyeomiji.view.util.NavigationOptionsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CategoryListAdapter(
    private val dataList: MutableMap<String, Int>,
    private val navOptions: NavOptions
) : RecyclerView.Adapter<CategoryListAdapter.ItemViewHolder>() {

    private var categoryButtonAnimator = CategoryButtonAnimation()

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryButton: TextView = view.findViewById(R.id.text_item_title)
        val linearItem: LinearLayout = view.findViewById(R.id.linear_item)
        val categoryImg: ImageView = view.findViewById(R.id.img_item_title)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryListAdapter.ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment_category_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CategoryListAdapter.ItemViewHolder, position: Int) {
        val itemName = dataList.keys.toList()[position]
        val itemImg = dataList.values.toList()[position]
        holder.categoryButton.text = itemName
        holder.categoryImg.setImageResource(itemImg)
        holder.linearItem.setOnClickListener {
            categoryButtonAnimator.startAnimation(it)
            val action = FragmentCategoryDirections.actionFragmentCategoryToFragmentLocationList()
            CoroutineScope(Main).launch{
                delay(500)
                holder.linearItem.findNavController().navigate(action, navOptions)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}