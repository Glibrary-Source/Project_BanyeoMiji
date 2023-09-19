package com.twproject.banyeomiji.view.util

import android.net.Uri
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorageManager {

    val firebaseStorage = FirebaseStorage.getInstance()

    private fun fireStoreCategoryImgListGet() : MutableList<Uri> {
        val testList = mutableListOf<Uri>()
        val rootRef = firebaseStorage.reference
        val imgRef = rootRef.child("locationcategory/")

        imgRef.listAll()
            .addOnSuccessListener {
                for (item in it.items) {
                    item.downloadUrl.addOnCompleteListener { task ->
                        testList.add(task.result)
                    }
                }
            }
        return testList
    }

}