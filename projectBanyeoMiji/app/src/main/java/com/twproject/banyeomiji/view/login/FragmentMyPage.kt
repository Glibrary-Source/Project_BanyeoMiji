package com.twproject.banyeomiji.view.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.twproject.banyeomiji.MyGlobals
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.FragmentMyPageBinding
import com.twproject.banyeomiji.vbutility.ButtonAnimation
import com.twproject.banyeomiji.view.login.util.GoogleLoginModule
import com.twproject.banyeomiji.view.login.util.GoogleObjectAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentMyPage : Fragment() {

    private lateinit var mContext: Context
    private lateinit var activity: LoginActivity

    private lateinit var binding: FragmentMyPageBinding

    private val googleLoginModule = GoogleLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private val db = Firebase.firestore
    private var loginState = "init"
    private var currentUid = "default"

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
        activity = mContext as LoginActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLoginStateAndUid()
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
                MyGlobals.instance!!.userLogin = 0
                auth.signOut()
                googleSignInClient.signOut()
                NaverIdLoginSDK.logout()

                withContext(Main) {
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.frame_fragment_host, FragmentLogin())
                    transaction.commit()
                }
            }
        }

        userDataCheckChange()

        binding.btnExtendChangeNickname.setOnClickListener {

            checkCounterNickName()

            val editChangeText = binding.editNickName.text

            var nickNameList = mutableListOf<String>()
            CoroutineScope(IO).launch { nickNameList = getAllUserNickName() }

            binding.btnChangeNickname.setOnClickListener {
                val nickname = editChangeText.toString().replace("\\s".toRegex(), "")

                if (nickname == "") {
                    Toast.makeText(mContext, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                } else if (nickNameList.contains(nickname)) {
                    Toast.makeText(mContext, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    changeNickName(nickname, editChangeText)
                }
            }
        }

        binding.frameMyReview.setOnClickListener {
            ButtonAnimation().startAnimation(it)
            val bundle = Bundle()
            bundle.putString("currentUid", currentUid)

            val myFragment = FragmentMyReview()
            myFragment.arguments = bundle

            CoroutineScope(Main).launch {
                delay(500)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_fragment_host, myFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    private fun getAllUserNickName(): MutableList<String> {
        val nickNameList = mutableListOf<String>()
        db.collection("user_db")
            .get()
            .addOnSuccessListener {
                for (doc in it.documents) {
                    nickNameList.add(doc.data!!["nickname"].toString())
                }
            }
        return nickNameList
    }

    private fun userDataCheckChange() {
        try {
            CoroutineScope(Main).launch{
                when (loginState) {
                    "google" -> {
                        val userDocRef = db.collection("user_db").document(auth.currentUser!!.uid)
                        userDocRef.addSnapshotListener { value, _ ->
                            if (value != null && value.exists()) {
                                val userData = value.data!!
                                binding.textEmail.text = userData["email"].toString()
                                binding.textNickName.text = userData["nickname"].toString()
                            }
                        }
                    }

                    "naver" -> {
                        NidOAuthLogin().callProfileApi(object :
                            NidProfileCallback<NidProfileResponse> {
                            override fun onSuccess(result: NidProfileResponse) {
                                val uid = result.profile?.id.toString()
                                val userDocRef = db.collection("user_db").document(uid)
                                userDocRef.addSnapshotListener { value, _ ->
                                    if (value != null && value.exists()) {
                                        val userData = value.data!!
                                        binding.textEmail.text = userData["email"].toString()
                                        binding.textNickName.text = userData["nickname"].toString()
                                    }
                                }
                            }

                            override fun onError(errorCode: Int, message: String) {}
                            override fun onFailure(httpStatus: Int, message: String) {}
                        })
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun setLoginStateAndUid() {
        if (auth.currentUser != null && MyGlobals.instance!!.userDataCheck == 1) {
            loginState = "google"
        } else if (NaverIdLoginSDK.getState().name != "NEED_LOGIN" && NaverIdLoginSDK.getState().name != "NEED_INIT" && NaverIdLoginSDK.getState().name != "NEED_REFRESH_TOKEN") {
            loginState = "naver"
        }
        setStateUid()
    }

    private fun setStateUid() {
        when (loginState) {
            "google" -> {
                currentUid = auth.currentUser!!.uid
            }

            "naver" -> {
                NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(result: NidProfileResponse) {
                        currentUid = result.profile?.id.toString()
                    }

                    override fun onError(errorCode: Int, message: String) {}
                    override fun onFailure(httpStatus: Int, message: String) {}
                })
            }
        }
    }

    private fun checkCounterNickName() {

        db.collection("user_db").document(currentUid)
            .get()
            .addOnSuccessListener {
                if (it.data!!["change_counter"] as Boolean) {
                    Toast.makeText(mContext, "이미 닉네임을 변경하셨습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    checkExpandNickNameView()
                    Toast.makeText(mContext, "닉네임 변경은 1회만 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkExpandNickNameView() {
        binding.frameChangeNickname.visibility =
            if (binding.frameChangeNickname.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun changeNickName(nickname: String, editChangeText: Editable) {
        val item = mapOf(
            "nickname" to nickname,
            "change_counter" to true
        )
        db.collection("user_db").document(currentUid)
            .update(item)
            .addOnSuccessListener {
                editChangeText.clear()
                binding.frameChangeNickname.visibility = View.GONE
                Toast.makeText(mContext, "닉네임 변경 완료", Toast.LENGTH_SHORT).show()
            }
    }

}