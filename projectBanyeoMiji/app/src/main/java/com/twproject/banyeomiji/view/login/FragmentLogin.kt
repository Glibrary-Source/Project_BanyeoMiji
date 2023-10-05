package com.twproject.banyeomiji.view.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentLoginBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import com.twproject.banyeomiji.vbutility.onThrottleClick
import com.twproject.banyeomiji.view.login.util.GoogleLoginModule
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import com.twproject.banyeomiji.view.login.util.EmailLoginModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mContext: Context
    private lateinit var activity: LoginActivity
    private lateinit var userSelectManager: UserSelectManager
    private lateinit var transaction: FragmentTransaction
    private lateinit var emailLoginModule: EmailLoginModule

    private val googleLoginModule = GoogleLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private var localState = 0
    private var email = " "
    private var password = " "
    private var signInContracts = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                googleLoginModule.firebaseAuthWithGoogle(
                    account,
                    activity,
                    auth,
                    userSelectManager,
                    transaction
                )
            } catch (_: ApiException) {}
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = context as LoginActivity

        emailLoginModule = EmailLoginModule(mContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userSelectManager = UserSelectManager(mContext.dataStore)

        CoroutineScope(IO).launch {
            userSelectManager.userLoginState.collect { state ->
                localState = state ?: 0
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)

        transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_fragment_host, FragmentMyPage())

        checkAlreadyLogin()
        initializeGoogleSignIn()

        setChangeListener()

        binding.btnLoginEmail.setOnClickListener {
            emailLoginModule.onlyEmailSignIn(email, password, transaction, userSelectManager)
        }

        binding.textSignUp.onThrottleClick {
            ButtonAnimation().startAnimation(it)

            val btnSignUp = binding.btnSignUp
            btnSignUp.visibility = View.VISIBLE
            btnSignUp.onThrottleClick {
                val pattern = Patterns.EMAIL_ADDRESS
                if(pattern.matcher(email).matches()) {
                    emailLoginModule.emailSignUp(email, password)
                } else {
                    Toast.makeText(mContext, "올바른 이메일을 작성해주세요", Toast.LENGTH_SHORT).show()
                }
            }

        }

        return binding.root
    }

    private fun checkAlreadyLogin() {
        if (localState == 1) {
            transaction.commit()
        }
    }

    private fun initializeGoogleSignIn() {
        val gso = googleLoginModule.getGoogleSignInOption(getString(R.string.default_web_client_id))
        val googleSignInClient = GoogleSignIn.getClient(mContext, gso)

        binding.btnLoginGoogle.onThrottleClick {
            ButtonAnimation().startAnimation(it)
            val signInIntent = googleSignInClient.signInIntent
            signInContracts.launch(signInIntent)
        }
    }

    private fun setChangeListener() {
        binding.editLoginEmail.addTextChangedListener {
            email = binding.editLoginEmail.text.toString()
        }
        binding.editLoginPassword.addTextChangedListener {
            password = binding.editLoginPassword.text.toString()
        }
    }


}

