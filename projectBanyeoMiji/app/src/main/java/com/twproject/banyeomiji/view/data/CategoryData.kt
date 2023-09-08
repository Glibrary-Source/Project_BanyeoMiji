package com.twproject.banyeomiji.view.data

import com.twproject.banyeomiji.R

class CategoryData {
    fun getCategoryList(): MutableMap<String, Int> {
        val categoryMap = mutableMapOf<String, Int>()
        categoryMap["문예회관"] = R.drawable.img_category_item_korea_gate
        categoryMap["카페"] = R.drawable.img_category_item_korea_cafe
        categoryMap["미술관"] = R.drawable.img_category_item_korea_art_gallery
        categoryMap["미용"] = R.drawable.img_category_item_korea_petsalon
        categoryMap["박물관"] = R.drawable.img_category_item_korea_museum
        categoryMap["반려동물용품"] = R.drawable.img_category_item_korea_dog_tools
        categoryMap["식당"] = R.drawable.img_category_item_korea_restaurant
        categoryMap["여행지"] = R.drawable.img_category_item_korea_trip
        categoryMap["위탁관리"] = R.drawable.img_category_item_korea_management
        categoryMap["펜션"] = R.drawable.img_category_item_korea_swimming_pool
        return categoryMap
    }
}