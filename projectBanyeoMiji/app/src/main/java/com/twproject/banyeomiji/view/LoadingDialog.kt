package com.twproject.banyeomiji.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.util.BackPressCallBackManager

class LoadingDialog constructor(context: Context): Dialog(context){
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {}

    init {
        setCanceledOnTouchOutside(false)

        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setContentView(R.layout.dialog_loading)
    }
}