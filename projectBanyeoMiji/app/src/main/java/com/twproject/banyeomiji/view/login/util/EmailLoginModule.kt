package com.twproject.banyeomiji.view.login.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
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

    // 이미 있는 email 로그인이 가능한지 확인
    fun onlyEmailSignIn(
        email: String,
        password: String,
        transaction: FragmentTransaction,
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emailVerificationCheck(
                        transaction
                    )
                } else {
                    Toast.makeText(mContext, "이메일 또는 비밀번호를 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 이메일 인증 확인 체크
    private fun emailVerificationCheck(
        transaction: FragmentTransaction
    ) {
        auth.currentUser!!.reload()
        if(auth.currentUser?.isEmailVerified == true) {
            transaction.commit()
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

    // 회원가입 코드
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

    // 새로 회원가입을 했을 때 임시 로그인 및 확인메일 발송
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

    // 회원가입한 아이디로 이메일 보내는 코드
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