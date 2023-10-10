package com.twproject.banyeomiji.view.main

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TempClass {

    val db = Firebase.firestore

    fun setDB() {
        val docId = hashMapOf<String, Any>(
            "DOC_ID" to "0"
        )
        db.collection("pet_location_data")
            .document("0")
            .update(docId)
            .addOnCompleteListener {
                Log.d("testFS", "성공")
            }
    }

    fun getDB() {
        val mutableDocList = mutableListOf<String>()
        db.collection("pet_location_data")
            .get()
            .addOnCompleteListener {
                for(document in it.result){
                    mutableDocList.add(document.id)
                }

                CoroutineScope(IO).launch {
                    for(docNumber in mutableDocList) {
                        delay(5)
                        val docId = hashMapOf<String, Any>(
                            "DOC_ID" to docNumber
                        )
                        db.collection("pet_location_data")
                            .document(docNumber)
                            .update(docId)
                            .addOnCompleteListener {
                                Log.d("testFS", "성공")
                            }
                            .addOnFailureListener {
                                Log.d("testFS", docNumber)
                            }
                    }
                }

                Log.d("testFS", "성공")


            }

    }
}