package com.twproject.banyeomiji.view.main.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.twproject.banyeomiji.view.main.adapter.LocationDataListAdapter
import com.twproject.banyeomiji.view.main.datamodel.PetLocationData

class AdapterStringManager {

    fun checkParking(item: PetLocationData): String {
        return if (item.PARKNG_POSBL_AT == "Y") "주차: 가능" else "주차: 불가능"
    }

    fun checkHomePage(item: PetLocationData, holder: LocationDataListAdapter.ItemViewHolder): String {
        return if (item.HMPG_URL == "") {
            holder.itemLink.setBackgroundColor(Color.parseColor("#CCCCCC"))
            "홈페이지 없음"
        } else {
            holder.itemLink.setBackgroundColor(Color.parseColor("#33b5e5"))
            "홈페이지"
        }
    }

    fun checkLimited(item: PetLocationData): String {
        return if (item.PET_LMTT_MTR_CN == "해당없음" || item.PET_LMTT_MTR_CN == "제한사항 없음") "제한 사항: 해당없음" else "제한 사항: ${item.PET_LMTT_MTR_CN}"
    }

    fun checkItemLink(item: PetLocationData, context: Context, holder: LocationDataListAdapter.ItemViewHolder) {
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

    fun checkAddress(item: PetLocationData): String {
        return if(item.RDNMADR_NM == "") return item.LNM_ADDR else item.RDNMADR_NM
    }

    fun checkRestDay(item: PetLocationData): String {
        return if(item.RSTDE_GUID_CN == "") return "정보 없음" else item.RSTDE_GUID_CN
    }

    fun checkOpenTime(item: PetLocationData): String {
        return if(item.OPER_TIME == "") return "정보 없음" else item.OPER_TIME
    }

    fun checkHotel(itemName: String) : String {
        return if(itemName == "펜션") "펜션 & 호텔" else itemName
    }
}