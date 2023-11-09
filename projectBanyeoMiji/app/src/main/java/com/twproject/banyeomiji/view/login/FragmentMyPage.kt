package com.twproject.banyeomiji.view.login

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.navercorp.nid.NaverIdLoginSDK
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

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var navController: NavController
    private lateinit var actionLoginPage: NavDirections

    private val googleLoginModule = GoogleLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private val db = Firebase.firestore
    private var loginState = "init"
    private var currentUid = "default"

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater)

        actionLoginPage = FragmentMyPageDirections.actionFragmentMyPageToFragmentLogin()
        navController = findNavController()

        val gso = googleLoginModule.getGoogleSignInOption(getString(R.string.default_web_client_id))
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.btnLogoutGoogle.setOnClickListener {
            CoroutineScope(IO).launch {
                MyGlobals.instance!!.userLogin = 0
                auth.signOut()
                googleSignInClient.signOut()
                NaverIdLoginSDK.logout()

                withContext(Main) {
                    navController.navigate(actionLoginPage)
                }
            }
        }

        setLoginStateAndUid()

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
                    userDataCheckChange()
                }
            }
        }

        binding.frameMyReview.setOnClickListener {
            ButtonAnimation().startAnimation(it)

            val actionMyReview = FragmentMyPageDirections.actionFragmentMyPageToFragmentMyReview(currentUid)

            CoroutineScope(Main).launch {
                delay(500)
                navController.navigate(actionMyReview)
            }
        }

        binding.btnUserResign.setOnClickListener {
            userShowResignDialog(googleSignInClient)
        }

        return binding.root
    }

    private fun setLoginStateAndUid() {
        loginState = if (auth.currentUser != null) {
            "google"
        } else {
            "wait"
        }
        setStateUid()
    }

    private fun setStateUid() {
        when (loginState) {
            "google" -> {
                currentUid = auth.currentUser!!.uid
                CoroutineScope(Main).launch { userDataCheckChange() }
            }
            "wait" -> {}
        }
    }

    private fun userDataCheckChange() {
        db.collection("user_db").document(currentUid)
            .get()
            .addOnSuccessListener { value ->
                val userData = value.data
                if (userData == null) {
                    userDataCheckChange()
                } else {
                    binding.textEmail.text = userData["email"].toString()
                    binding.textNickName.text = userData["nickname"].toString()
                }
            }
            .addOnFailureListener {
                Toast.makeText(mContext, "불러오기 실패.", Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
                Toast.makeText(mContext, "불러오기 실패.", Toast.LENGTH_SHORT).show()
            }

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

    private fun checkCounterNickName() {
        when (currentUid) {
            "wait" -> {
                Toast.makeText(mContext, "잠시 기다렸다 다시시도해주세요.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                db.collection("user_db").document(currentUid)
                    .get()
                    .addOnSuccessListener {
                        val data = it.data
                        if (data == null) {
                            Toast.makeText(mContext, "잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                        } else if (it.data!!["change_counter"] as Boolean) {
                            Toast.makeText(mContext, "이미 닉네임을 변경하셨습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            checkExpandNickNameView()
                            Toast.makeText(mContext, "닉네임 변경은 1회만 가능합니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
        }
    }

    private fun checkExpandNickNameView() {
        binding.frameChangeNickname.visibility =
            if (binding.frameChangeNickname.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun changeNickName(nickname: String, editChangeText: Editable) {
        when (currentUid) {
            "wait" -> {
                Toast.makeText(mContext, "잠시 기다려 주세요.", Toast.LENGTH_SHORT).show()
            }
            else -> {
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
    }

    private fun userShowResignDialog(googleSignInClient: GoogleSignInClient) {
        val builder = AlertDialog.Builder(context)
        val eraseDialog =
            builder.setTitle("반려미지 회원탈퇴 알림")
                .setMessage("정말 회원을 탈퇴 하시겠습니까?")
                .setPositiveButton("탈퇴") { _, _ ->
                    binding.btnUserResign.isEnabled = false
                    binding.btnChangeNickname.isEnabled = false
                    binding.btnLogoutGoogle.isEnabled = false
                    binding.textMyReview.isEnabled = false

                    userResign(googleSignInClient)
                }
                .setNegativeButton("취소") { _, _ -> }
                .setCancelable(false)
                .create()
        eraseDialog.show()
    }

    private fun userResign(googleSignInClient: GoogleSignInClient) {
        when (loginState) {
            "wait" -> {
                Toast.makeText(mContext, "잠시 기다려 주세요.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                db.collection("user_db").document(currentUid)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(mContext, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                        MyGlobals.instance!!.userLogin = 0
                        logoutAction(googleSignInClient)
                        try {
                            navController.navigate(actionLoginPage)
                        } catch (_: Exception) {
                            Toast.makeText(mContext, "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                        }


                    }
            }
        }
    }

    private fun logoutAction(googleSignInClient: GoogleSignInClient) {
        when (loginState) {
            "google" -> {
                try {
                    auth.currentUser!!.delete()
                    googleSignInClient.signOut()
                    NaverIdLoginSDK.logout()
                } catch (_: Exception) {
                }
            }
            else -> {

            }
        }
    }

}