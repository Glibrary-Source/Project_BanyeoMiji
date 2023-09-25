package com.twproject.banyeomiji.view.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentLoginBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

//class FragmentLogin : Fragment() {
//
//    private lateinit var mContext: Context
//    private lateinit var activity: LoginActivity
//
//    private lateinit var binding : FragmentLoginBinding
//    private lateinit var googleLoginManager: GoogleLoginManager
//    private lateinit var userSelectManager: UserSelectManager
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//
//        mContext = context
//        activity = context as LoginActivity
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        userSelectManager = UserSelectManager(mContext.dataStore)
//        googleLoginManager = GoogleLoginManager(mContext, userSelectManager)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentLoginBinding.inflate(inflater)
//
//        CoroutineScope(Main).launch {
//            userSelectManager.userLoginState.collect {state ->
//                Log.d("testState", state.toString())
//                if(state == 1) {
//                    Log.d("testState", state.toString())
//                    val transaction = parentFragmentManager.beginTransaction()
//                    transaction.replace(R.id.frame_fragment_host, FragmentMyPage())
////        transaction.addToBackStack(null)
//                    transaction.commit()
//                }
//            }
//        }
//
//        binding.btnLoginGoogle.setOnClickListener {
//            googleLoginManager.performGoogleLogin()
//        }
//
//        return binding.root
//    }
//}

class FragmentLogin : Fragment() {

    private val TAG = "LoginTest"

    private lateinit var mContext: Context

    private lateinit var binding: FragmentLoginBinding
    private lateinit var userSelectManager: UserSelectManager

    private lateinit var navController: NavController
    private var localState by Delegates.notNull<Int>()

    private val auth = FirebaseAuth.getInstance()
    private var signInContracts = registerForActivityResult(
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSelectManager = UserSelectManager(mContext.dataStore)
        navController = findNavController()

        CoroutineScope(IO).launch {
            userSelectManager.userLoginState.collect { state ->
                localState = state!!
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        if(localState == 1) {
            val action = FragmentLoginDirections.actionFragmentLoginToFragmentMyPage()
            navController.navigate(action)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(mContext, gso)

        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInContracts.launch(signInIntent)
        }

        return binding.root
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase 로그인 성공: ${auth.currentUser?.email}")
                    val action = FragmentLoginDirections.actionFragmentLoginToFragmentMyPage()
                    navController.navigate(action)
                    CoroutineScope(IO).launch {
                        userSelectManager.setLoginState(1)
                    }
                } else {
                    Log.w(TAG, "Firebase 로그인 실패: ${task.exception?.message}")
                }
            }
    }
}

