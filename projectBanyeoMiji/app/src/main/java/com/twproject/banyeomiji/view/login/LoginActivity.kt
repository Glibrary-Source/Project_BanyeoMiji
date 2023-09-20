package com.twproject.banyeomiji.view.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivityLoginBinding
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val TAG = "RemoteMainActivity"
        const val REMOTE_KEY_APP_VERSION = "app_version"
        const val REMOTE_KEY_WELCOME_MSG = "welcome_msg"
        const val REMOTE_KEY_TEST_SHOW_ENABLE= "test_show_enable"
        const val REMOTE_KEY_TEST_TITLE = "test_title"
        const val REMOTE_KEY_TEST_MSG = "test_msg"
        const val REMOTE_KEY_DIALOG_INFO = "dialog_info"
    }

    private var appVersion: String? = null
    private var welcomeMsg: String? = null
    private var testShowEnable: Boolean = false
    private var testTitle: String? = null
    private var testMsg: String? = null
    private var dialogInfo: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        initRemoteConfig()
//        initView()

    }

    /**
     * initRemoteConfig()
     *  - Firebase Remote Config 설정
     */
    private fun initRemoteConfig() {

        val remoteConfig = Firebase.remoteConfig  //Remote Config 객체 가져오기
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            mapOf(
                REMOTE_KEY_APP_VERSION to "0.0.0",
                REMOTE_KEY_WELCOME_MSG to "Welcome to my awesome app!"
            )
        }

        remoteConfig.setConfigSettingsAsync(configSettings)  //객체 default 값 설정 - map 형태

        //매개변수 KEY 값 설정
        appVersion = remoteConfig.getString(REMOTE_KEY_APP_VERSION)
        welcomeMsg = remoteConfig.getString(REMOTE_KEY_WELCOME_MSG)
        testShowEnable = remoteConfig.getBoolean(REMOTE_KEY_TEST_SHOW_ENABLE)
        testTitle = remoteConfig.getString(REMOTE_KEY_TEST_TITLE)
        testMsg = remoteConfig.getString(REMOTE_KEY_TEST_MSG)
        dialogInfo = remoteConfig.getString(REMOTE_KEY_DIALOG_INFO)

        //값 가져오기 및 활성화
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Fetch failed",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }


    /**
     * initView()
     *  - View 설정
     */
    private fun initView() {

        // button show test 클릭 시
        binding.btnShowTest.setOnClickListener {

            binding.tvTest.text =
                if(testShowEnable) {
                    "$REMOTE_KEY_WELCOME_MSG : ${welcomeMsg.toString()}" +
                            "\n\n" +
                            "$REMOTE_KEY_APP_VERSION : ${appVersion.toString()}" +
                            "\n\n" +
                            "$REMOTE_KEY_TEST_SHOW_ENABLE : ${testShowEnable.toString()}" +
                            "\n\n" +
                            "$REMOTE_KEY_TEST_TITLE : $testTitle" +
                            "\n\n" +
                            "$REMOTE_KEY_TEST_MSG : $testMsg"
                } else {
                    "$REMOTE_KEY_TEST_SHOW_ENABLE : ${testShowEnable.toString()}"
                }

        }

        // button show dialog 클릭 시
        binding.btnShowDialog.setOnClickListener {

            val dialogInfoJson = JSONObject(dialogInfo)  //dialogInfo 를 JSONObject 로 변환

            if(dialogInfoJson.getBoolean("show_enable")) {

                val builder = AlertDialog.Builder(this)
                val dialog = builder.setTitle("[${this.getString(R.string.app_name)}] ${dialogInfoJson.get("dialog_title")}")
                    .setMessage("${dialogInfoJson.get("dialog_msg")}")
                    .setNegativeButton("닫기", null)
                    .setCancelable(false)
                    .create()
                dialog.show()

            } else {
                Toast.makeText(this, "Dialog Info - show_enable :: false", Toast.LENGTH_SHORT).show()
            }

        }
    }

}