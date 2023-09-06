package com.twproject.banyeomiji.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.datamodel.PetLocationData

class LocationDataListAdapter(
    private val cafeData: MutableList<PetLocationData>,
    private val context: Context
) : RecyclerView.Adapter<LocationDataListAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView = view.findViewById(R.id.text_location_item_title)
        val itemAddress: TextView = view.findViewById(R.id.text_location_item_address)
        val itemRestDay: TextView = view.findViewById(R.id.text_location_item_rest_day)
        val itemPark: TextView = view.findViewById(R.id.text_location_item_parking)
        val itemOpenTime: TextView = view.findViewById(R.id.text_location_item_open_time)
        val itemLink: Button = view.findViewById(R.id.text_location_item_link)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationDataListAdapter.ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fragment_location_list, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocationDataListAdapter.ItemViewHolder, position: Int) {
        val item = cafeData[position]
        holder.itemTitle.text = item.FCLTY_NM
        holder.itemAddress.text = "주소: ${item.RDNMADR_NM}"
        holder.itemRestDay.text = "휴일: ${item.RSTDE_GUID_CN}"
        holder.itemPark.text = checkParking(item)
        holder.itemOpenTime.text = "영업 시간: ${item.OPER_TIME}"
        holder.itemLink.text = checkHomePage(item)
        holder.itemLink.setOnClickListener {
            try {
                if (item.HMPG_URL != "") {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(item.HMPG_URL)
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                try{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://${item.HMPG_URL}")
                    context.startActivity(intent)
                } catch (e: Exception) {
                    holder.itemLink.text = "홈페이지 없음"
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return cafeData.size
    }
}

private fun checkParking(item: PetLocationData): String {
    return if (item.PARKNG_POSBL_AT == "Y") "주차: 가능" else "주차: 불가능"
}

private fun checkHomePage(item: PetLocationData): String {
    return if (item.HMPG_URL == "") "홈페이지 없음" else "홈페이지"
}

