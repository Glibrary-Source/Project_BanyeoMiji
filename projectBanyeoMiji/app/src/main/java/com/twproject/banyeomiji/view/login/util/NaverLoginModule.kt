package com.twproject.banyeomiji.view.login.util

import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.datastore.UserSelectManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class NaverLoginModule {

    private val db = Firebase.firestore

    fun getOAuthLoginCallback(
        transaction: FragmentTransaction,
//        userSelectManager: UserSelectManager
    ): OAuthLoginCallback {
        return object : OAuthLoginCallback {
            override fun onSuccess() {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        val email: String = result.profile?.email.toString()
                        val uid: String = result.profile?.id.toString()
                        CoroutineScope(Main).launch {
                            setUserDb(uid, email, transaction,
//                                userSelectManager
                            )
                        }
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
        transaction: FragmentTransaction,
//        userSelectManager: UserSelectManager
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
                            transaction.commit()
//                            CoroutineScope(IO).launch { userSelectManager.setLoginState(1) }
                            MyGlobals.instance!!.userLogin = 1
                        }
                        .addOnFailureListener {}
                } else {
                    transaction.commit()
//                    CoroutineScope(IO).launch { userSelectManager.setLoginState(1) }
                    MyGlobals.instance!!.userLogin = 1
                }
            }
            .addOnFailureListener {}
    }
}