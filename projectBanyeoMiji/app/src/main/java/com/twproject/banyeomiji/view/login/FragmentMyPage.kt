package com.twproject.banyeomiji.view.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twproject.banyeomiji.MyGlobals
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

class FragmentMyPage : Fragment() {

    private lateinit var mContext: Context

    private lateinit var binding: FragmentMyPageBinding
    private lateinit var userSelectManager: UserSelectManager

    private val googleLoginModule = GoogleLoginModule()
    private val auth = GoogleObjectAuth.getFirebaseAuth()
    private val db = Firebase.firestore

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

        val userUid = GoogleObjectAuth.mAuth.currentUser?.uid
        if (userUid != null) {
            db.collection("user_db").document(userUid)
                .get()
                .addOnSuccessListener { user ->
                    MyGlobals.instance!!.userNickName = user.data!!["nickname"].toString()
                }
        }

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

        CoroutineScope(Main).launch { userDataCheckChange() }

        binding.btnExtendChangeNickname.setOnClickListener {
            checkExpandNickNameView()
            val editChangeText = binding.editNickName.text
            binding.btnChangeNickname.setOnClickListener {
                val nickname = editChangeText.toString()
                if (nickname == "") {
                    Toast.makeText(mContext, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    editChangeText.clear()

                    val uid = auth.currentUser!!.uid

                    db.collection("user_db").document(uid)
                        .update("nickname", nickname)
                        .addOnSuccessListener {
                            nickNameChangeGlobal(nickname)
                        }
                    binding.frameChangeNickname.visibility = View.GONE
                }

            }
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private suspend fun userDataCheckChange() {
        try {
            val userDocRef = db.collection("user_db").document(auth.currentUser!!.uid)
            userDocRef.addSnapshotListener { value, _ ->
//            if(error != null ) {}
                if (value != null && value.exists()) {
                    val userData = value.data!!
                    binding.textEmail.text = userData["email"].toString()
                    binding.textNickName.text = userData["nickname"].toString()
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun checkExpandNickNameView() {
        binding.frameChangeNickname.visibility =
            if (binding.frameChangeNickname.visibility == View.VISIBLE) View.GONE else View.VISIBLE

    }

    private fun nickNameChangeGlobal(nickname: String) {
        MyGlobals.instance!!.userNickName = nickname
    }

}