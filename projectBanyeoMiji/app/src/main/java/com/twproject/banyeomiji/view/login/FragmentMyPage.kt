package com.twproject.banyeomiji.view.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentMyPageBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import com.twproject.banyeomiji.view.login.util.GoogleLoginModule
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentMyPage : Fragment() {

    private lateinit var mContext: Context

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var userSelectManager: UserSelectManager

    private val googleLoginModule = GoogleLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSelectManager = UserSelectManager(mContext.dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater)

        val gso = googleLoginModule.getGoogleSignInOption(getString(R.string.default_web_client_id))
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.btnLogoutGoogle.setOnClickListener {

            CoroutineScope(IO).launch {
                userSelectManager.setLoginState(0)
                auth.signOut()
                googleSignInClient.signOut()
            }

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_fragment_host, FragmentLogin())
            transaction.commit()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}