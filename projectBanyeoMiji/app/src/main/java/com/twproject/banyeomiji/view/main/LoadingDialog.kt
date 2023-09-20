package com.twproject.banyeomiji.view.main

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.twproject.banyeomiji.R

class LoadingDialog constructor(context: Context): Dialog(context){
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}

    init {
        setCanceledOnTouchOutside(false)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_loading)
    }
}