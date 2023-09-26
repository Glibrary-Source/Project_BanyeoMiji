package com.twproject.banyeomiji.view.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentLoginBinding
import com.twproject.banyeomiji.datastore.UserSelectManager
import com.twproject.banyeomiji.datastore.dataStore
import com.twproject.banyeomiji.vbutility.onThrottleClick
import com.twproject.banyeomiji.view.login.util.GoogleLoginModule
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mContext: Context
    private lateinit var activity: LoginActivity
    private lateinit var userSelectManager: UserSelectManager
    private lateinit var transaction: FragmentTransaction

    private var localState = 0
    private val googleLoginModule = GoogleLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private var email = ""
    private var password = ""

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
            } catch (_: ApiException) {
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        activity = context as LoginActivity
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

        setChangeListener()

        binding.textSignUp.onThrottleClick {
            ButtonAnimation().startAnimation(it)

            val btnSignUp = binding.btnSignUp
            btnSignUp.visibility = View.VISIBLE
            btnSignUp.onThrottleClick {
                emailSignUp(email, password)
            }

        }

        binding.btnLoginEmail.setOnClickListener {
            onlyEmailSignIn(email, password)
        }

        binding.btnCheckCurrent.setOnClickListener {
            auth.currentUser!!.reload()
            Log.d("testCurrent", auth.currentUser!!.email.toString())
            Log.d("testCheck", auth.currentUser!!.isEmailVerified.toString())
        }


        transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_fragment_host, FragmentMyPage())

        checkAlreadyLogin()
        initializeGoogleSignIn()


        return binding.root
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

    private fun checkAlreadyLogin() {
        if (localState == 1) {
            transaction.commit()
        }
    }

    // 이미 있는 email 로그인이 가능한지 확인
    private fun onlyEmailSignIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(mContext, "로그인 성공", Toast.LENGTH_SHORT).show()
                    transaction.commit()
                } else {
                    Toast.makeText(mContext, "이메일을 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 새로 회원가입을 했을 때 임시 로그인 및 확인메일 발송
    private fun emailSignInAndCheck(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authEmailCheck()
                } else {
                    Toast.makeText(mContext, "이메일을 확인해 주세요", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 회원가입 코드
    private fun emailSignUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    emailSignInAndCheck(email, password)
                } else {
                    Toast.makeText(mContext, "회원가입 실패", Toast.LENGTH_LONG).show()
                }
            }
    }

    // 회원가입한 아이디로 이메일 보내는 코드
    private fun authEmailCheck() {
        auth.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                if(it.isSuccessful) {
                    Toast.makeText(mContext, "이메일을 인증해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    currentUserDelete()
                    auth.signOut()
                }
            }
    }

    // 이메일 인증이 안되면 아이디삭제
    private fun currentUserDelete() {
        auth.currentUser?.delete()
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

