package com.twproject.banyeomiji.view.login.util

import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleLoginModule {

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
        userSelectManager: UserSelectManager,
        transaction: FragmentTransaction
    ) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        userSelectManager.setLoginState(1)
                    }
                    transaction.commit()
                } else {
                    Toast.makeText(activity,"인터넷 연결을 확인해주세요",Toast.LENGTH_SHORT).show()
                }
            }
    }


}