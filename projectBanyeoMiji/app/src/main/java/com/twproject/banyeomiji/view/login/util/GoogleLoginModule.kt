package com.twproject.banyeomiji.view.login.util

import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleLoginModule {

    private val db = Firebase.firestore
    fun getGoogleSignInOption(idToken: String) : GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(idToken)
            .requestEmail()
            .build()
    }

    fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount,
        activity: LoginActivity,
        auth: FirebaseAuth,
        transaction: FragmentTransaction
    ) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        MyGlobals.instance!!.userLogin = 1
                        MyGlobals.instance!!.userDataCheck = 1
                        val currentUser = auth.currentUser
                        setUserDb(currentUser!!.uid, currentUser.email!!.toString())
                    }
                    transaction.commit()
                } else {
                    Toast.makeText(activity,"인터넷 연결을 확인해주세요",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setUserDb(uid: String, email: String) {
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