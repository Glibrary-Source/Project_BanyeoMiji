package com.twproject.banyeomiji.view.login.util

import com.google.firebase.auth.FirebaseAuth

object GoogleObjectAuth {
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getFirebaseAuth(): FirebaseAuth {
        return mAuth
    }
}