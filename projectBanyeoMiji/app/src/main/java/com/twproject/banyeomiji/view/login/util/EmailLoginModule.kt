package com.twproject.banyeomiji.view.login.util

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.MyGlobals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmailLoginModule(
    private val mContext: Context
) {

    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private val db = Firebase.firestore

    fun onlyEmailSignIn(
        email: String,
        password: String,
        navController: NavController,
        action: NavDirections
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emailVerificationCheck(
                        navController,
                        action
                    )
                } else {
                    Toast.makeText(mContext, "이메일 또는 비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun emailVerificationCheck(
        navController: NavController,
        action: NavDirections
    ) {
        auth.currentUser!!.reload()
        if(auth.currentUser?.isEmailVerified == true) {
            navController.navigate(action)
            CoroutineScope(Dispatchers.IO).launch {
                MyGlobals.instance!!.userLogin = 1
                MyGlobals.instance!!.userDataCheck = 1
                val currentUser = auth.currentUser
                setUserDb(currentUser!!.uid, currentUser.email!!)
            }
            Toast.makeText(mContext, "로그인 성공", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(mContext, "이메일인증을 진행해주세요", Toast.LENGTH_SHORT).show()
        }
    }

    fun emailSignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emailSignInAndCheck(email, password)
                } else {
                    Toast.makeText(mContext, "인증이 진행중 입니다", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun emailSignInAndCheck(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    verificationEmailSend()
                } else {
                    Toast.makeText(mContext, "이메일을 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun verificationEmailSend() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(mContext, "이메일을 인증해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "인증을 다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private suspend fun setUserDb(uid: String, email: String) {
        val setUserData = hashMapOf(
            "uid" to uid,
            "email" to email,
            "nickname" to "기본닉네임",
            "change_counter" to false
        )
        db.collection("user_db").document(uid)
            .get()
            .addOnSuccessListener { user ->
                if(!user.exists()) {
                    db.collection("user_db")
                        .document(uid)
                        .set(setUserData)
                }
            }
    }
}