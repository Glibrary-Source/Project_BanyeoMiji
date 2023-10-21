package com.twproject.banyeomiji.view.login

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Patterns
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
import com.navercorp.nid.NaverIdLoginSDK
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentLoginBinding
import com.twproject.banyeomiji.vbutility.onThrottleClick
import com.twproject.banyeomiji.view.login.util.GoogleLoginModule
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import com.twproject.banyeomiji.view.login.util.EmailLoginModule
import com.twproject.banyeomiji.view.login.util.NaverLoginModule

class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var mContext: Context
    private lateinit var activity: LoginActivity
    private lateinit var transaction: FragmentTransaction
    private lateinit var emailLoginModule: EmailLoginModule

    private val googleLoginModule = GoogleLoginModule()
    private val naverLoginModule = NaverLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()
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
            emailLoginModule.onlyEmailSignIn(
                email,
                password,
                transaction
            )
        }

        binding.btnLoginNaver.setOnClickListener {
            ButtonAnimation().startAnimation(it)

            NaverIdLoginSDK.authenticate(mContext, naverLoginModule.getOAuthLoginCallback(transaction))
        }

        binding.textSignUp.onThrottleClick {
            ButtonAnimation().startAnimation(it)

            val btnSignUp = binding.btnSignUp
            signUpVisibleControl()
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
        if (MyGlobals.instance!!.userLogin == 1) {
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

    private fun signUpVisibleControl() {
        binding.btnSignUp.visibility = if(binding.btnSignUp.visibility == View.GONE) View.VISIBLE else View.GONE
    }

}

