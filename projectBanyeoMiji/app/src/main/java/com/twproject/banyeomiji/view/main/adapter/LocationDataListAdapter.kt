package com.twproject.banyeomiji.view.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentLocationListBinding
import com.twproject.banyeomiji.view.main.datamodel.PetLocationData
import com.twproject.banyeomiji.view.main.util.AdapterStringManager

class LocationDataListAdapter(
    private val cafeData: MutableList<PetLocationData>,
    private val context: Context,
    private val binding: FragmentLocationListBinding
) : RecyclerView.Adapter<LocationDataListAdapter.ItemViewHolder>() {

    private val stringManager = AdapterStringManager()

    init {
        checkEmptyData()
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView = view.findViewById(R.id.text_location_item_title)
        val itemAddress: TextView = view.findViewById(R.id.text_location_item_address)
        val itemRestDay: TextView = view.findViewById(R.id.text_location_item_rest_day)
        val itemOpenTime: TextView = view.findViewById(R.id.text_location_item_open_time)
        val itemLimit: TextView = view.findViewById(R.id.text_location_item_limit)
        val itemPark: TextView = view.findViewById(R.id.text_location_item_parking)
        val itemLink: Button = view.findViewById(R.id.text_location_item_link)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment_location_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = cafeData[position]
        holder.itemTitle.text = item.FCLTY_NM
        holder.itemAddress.text = "주소: ${stringManager.checkAddress(item)}"
        holder.itemRestDay.text = "휴일: ${item.RSTDE_GUID_CN}"
        holder.itemOpenTime.text = "영업 시간: ${item.OPER_TIME}"
        holder.itemLimit.text = stringManager.checkLimited(item)
        holder.itemPark.text = stringManager.checkParking(item)
        holder.itemLink.text = stringManager.checkHomePage(item, holder)
        holder.itemLink.setOnClickListener {
            stringManager.checkItemLink(item, context, holder)
        }
    }

    override fun getItemCount(): Int {
        return cafeData.size
    }

    private fun checkEmptyData() {
        if(cafeData.size == 0) { binding.textEmptyList.visibility = View.VISIBLE}
    }
}
