package com.twproject.banyeomiji.view.login.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GoogleLoginManager(
    context: Context,
    private val userSelectManager: UserSelectManager
) {
    private val TAG = "testLoginModule"
    private val activity = context as LoginActivity
    private val auth = FirebaseAuth.getInstance()
    private val gso =  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(activity.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    private val googleSignInClient = GoogleSignIn.getClient(context, gso)

    private val signInContracts: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign-in was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)

                Log.d(TAG, "Google Sign-in successful: ${account.email}")
                // Perform further actions or notify the view
            } catch (e: ApiException) {
                // Google Sign-in failed, handle the error
                Log.w(TAG, "Google Sign-in failed: ${e.message}")
                // Perform error handling or notify the view
            }
        }
    }

    fun performGoogleLogin() {
        try {
            val signInIntent = googleSignInClient.signInIntent
            signInContracts.launch(signInIntent)
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    fun performGoogleLogout() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase 로그인 성공: ${auth.currentUser?.email}")
                    CoroutineScope(IO).launch{
                        userSelectManager.setLoginState(1)
                        userSelectManager.userLoginState.collect {
                            Log.d("", it.toString())
                        }
                    }
                } else {
                    Log.w(TAG, "Firebase 로그인 실패: ${task.exception?.message}")
                }
            }
    }
}