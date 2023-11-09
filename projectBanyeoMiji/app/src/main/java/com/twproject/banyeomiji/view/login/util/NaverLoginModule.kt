package com.twproject.banyeomiji.view.login.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.twproject.banyeomiji.MyGlobals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NaverLoginModule(
) {
    private val db = Firebase.firestore
    private val auth = GoogleObjectAuth.getFirebaseAuth()

    fun getOAuthLoginCallback(
        navController: NavController,
        action: NavDirections
    ): OAuthLoginCallback {
        return object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        GoogleObjectAuth.getFirebaseAuth().signOut()
                        val email: String = result.profile?.email.toString()
                        val uid: String = result.profile?.id.toString()
                        onlyNaverEmailSignIn(email, uid, navController, action)
                    }

                    override fun onError(errorCode: Int, message: String) {}
                    override fun onFailure(httpStatus: Int, message: String) {}
                })
            }
            override fun onError(errorCode: Int, message: String) {}
            override fun onFailure(httpStatus: Int, message: String) {}
        }
    }

    fun onlyNaverEmailSignIn(
        email: String,
        password: String,
        navController: NavController,
        action: NavDirections
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navController.navigate(action)
                    MyGlobals.instance!!.userLogin = 1
                } else {
                    naverEmailSignUp(email, password, navController, action)
                }
            }
    }

    private fun naverEmailSignUp(
        email: String,
        password: String,
        navController: NavController,
        action: NavDirections
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch{
                        val currentUser = auth.currentUser!!
                        setUserDb(currentUser.uid, email, navController, action)
                    }
                }
            }
    }

    private suspend fun setUserDb(
        uid: String,
        email: String,
        navController: NavController,
        action: NavDirections
    ) {
        val setUserData = hashMapOf(
            "uid" to uid,
            "email" to email,
            "nickname" to "기본닉네임",
            "change_counter" to false
        )
        db.collection("user_db").document(uid)
            .get()
            .addOnSuccessListener { user ->
                if (!user.exists()) {
                    db.collection("user_db")
                        .document(uid)
                        .set(setUserData)
                        .addOnSuccessListener {
                            navController.navigate(action)
                            MyGlobals.instance!!.userLogin = 1
                        }
                        .addOnFailureListener {}
                } else {
                    navController.navigate(action)
                    MyGlobals.instance!!.userLogin = 1
                }
            }
            .addOnFailureListener {}
    }
}