package com.twproject.banyeomiji.view.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

            privateDataAgreement()
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
        binding.btnSignUp.visibility = View.VISIBLE
    }

    private fun privateDataAgreement() {
        val agreementText = "개인정보 처리방침"
        val message = SpannableString("개인정보 처리방침을 읽어보시고 동의해주세요.")
        val webUrl = "https://sites.google.com/view/banyeomiji-privacy-policy"

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
            }
        }

        val agreementStart = message.indexOf(agreementText)
        val agreementEnd = agreementStart + agreementText.length

        message.setSpan(clickableSpan, agreementStart, agreementEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val builder = AlertDialog.Builder(context)
        val agreementDialog =
            builder
                .setTitle("반려미지 개인정보 동의 알림")
                .setMessage(message)
                .setPositiveButton("동의") { _, _ ->
                    signUpVisibleControl()
                    val btnSignUp = binding.btnSignUp
                    btnSignUp.onThrottleClick {
                        val pattern = Patterns.EMAIL_ADDRESS
                        if(pattern.matcher(email).matches()) {
                            emailLoginModule.emailSignUp(email, password)
                        } else {
                            Toast.makeText(mContext, "올바른 이메일을 작성해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("취소") { _, _ ->
                    Toast.makeText(mContext, "개인정보 동의를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
                .setCancelable(false)
                .create()

        agreementDialog.show()
        agreementDialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    }
}