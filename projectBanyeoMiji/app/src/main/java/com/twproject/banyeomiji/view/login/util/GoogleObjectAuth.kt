package com.twproject.banyeomiji.view.login.util

import com.google.firebase.auth.FirebaseAuth

object GoogleObjectAuth {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getFirebaseAuth(): FirebaseAuth {
        return mAuth
    }
}