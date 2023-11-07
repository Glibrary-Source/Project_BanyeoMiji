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
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class NaverLoginModule {

    private val db = Firebase.firestore

    fun getOAuthLoginCallback(
        navController: NavController,
        action: NavDirections
    ): OAuthLoginCallback {
        return object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val email: String = result.profile?.email.toString()
                        val uid: String = result.profile?.id.toString()
                        CoroutineScope(Main).launch {
                            setUserDb(uid, email, navController, action)
                        }
                        MyGlobals.instance!!.userDataCheck = 0
                        GoogleObjectAuth.getFirebaseAuth().signOut()
                    }

                    override fun onError(errorCode: Int, message: String) {}
                    override fun onFailure(httpStatus: Int, message: String) {}
                })
            }

            override fun onError(errorCode: Int, message: String) {}
            override fun onFailure(httpStatus: Int, message: String) {}
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