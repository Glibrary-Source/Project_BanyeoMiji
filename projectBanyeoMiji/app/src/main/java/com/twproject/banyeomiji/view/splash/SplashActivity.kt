package com.twproject.banyeomiji.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.databinding.ActivitySplashBinding
import com.twproject.banyeomiji.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    companion object {
        const val TAG = "RemoteMainActivity"
        const val REMOTE_KEY_APP_VERSION = "app_version"
        const val REMOTE_KEY_DIALOG_INFO = "dialog_info"
        const val REMOTE_KEY_UPDATE_EMERGENCY = "update_emergency"
        const val REMOTE_KEY_UPDATE_NORMAL = "update_normal"
    }

    private var appVersion: String? = null
    private var dialogInfo: String? = null
    private var updateEmergency: Boolean = false
    private var updateNormal: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash)
        val intent = Intent(this, MainActivity::class.java)

//        CoroutineScope(Main).launch {
//            delay(2000)
//            startActivity(intent)
//            finish()
//        }

        initRemoteConfig()
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
                //default 값 설정
                REMOTE_KEY_APP_VERSION to "0.0.0",
                REMOTE_KEY_UPDATE_EMERGENCY to false,
                REMOTE_KEY_UPDATE_NORMAL to false
            )
        }

        remoteConfig.setConfigSettingsAsync(configSettings)  //객체 default 값 설정 - map 형태
//        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)  //객체 default 값 설정 - xml 형태

        //값 가져 오기 및 활성화
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //매개 변수 KEY 값 설정
                    updateEmergency = remoteConfig.getBoolean(REMOTE_KEY_UPDATE_EMERGENCY)
                    updateNormal = remoteConfig.getBoolean(REMOTE_KEY_UPDATE_NORMAL)
                    dialogInfo = remoteConfig.getString(REMOTE_KEY_DIALOG_INFO)

                    emergencyAlert(updateEmergency, dialogInfo)
                    normalAlert(updateNormal)

                    Toast.makeText(this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Fetch failed",
                        Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun normalAlert(
        updateNormal: Boolean
    ) {
       if(updateNormal) {
           val dialogInfoJson = JSONObject(dialogInfo)

           val builder = AlertDialog.Builder(this)
           val dialog = builder.setTitle("[${this.getString(R.string.app_name)}] ${dialogInfoJson.get("dialog_title")}")
               .setMessage("업데이트를 해주세요")
               .setNegativeButton("닫기")
                { _, _ ->
                    CoroutineScope(Main).launch {
                        delay(2000)
                        startActivity(intent)
                        finish()
                    }
                }
               .setPositiveButton("연결")
               { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.kapitalletter.wardoffice"))
                    startActivity(intent)
                    finish()
               }
               .setCancelable(false)
               .create()
           dialog.show()
       } else {
           CoroutineScope(Main).launch {
               delay(2000)
               startActivity(intent)
               finish()
           }
       }
    }

    private fun emergencyAlert(
        updateEmergency: Boolean,
        dialogInfo: String?
    ) {
        if(updateEmergency) {
            val dialogInfoJson = JSONObject(dialogInfo)

            val builder = AlertDialog.Builder(this)
            val dialog = builder.setTitle("[${this.getString(R.string.app_name)}] ${dialogInfoJson.get("dialog_title")}")
                .setMessage("업데이트를 해주세요")
                .setNegativeButton("연결", null)
//                { _, _ ->
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.kapitalletter.wardoffice"))
//                    startActivity(intent)
//                    finish()
//                }
                .setCancelable(false)
                .create()
            dialog.show()
        } else {
            CoroutineScope(Main).launch {
                delay(2000)
                startActivity(intent)
                finish()
            }
        }
    }

    /**
     * initView()
     *  - View 설정
     */
//    private fun initView() {
//
//        // button show test 클릭 시
//        binding.btnShowTest.setOnClickListener {
//
//            binding.tvTest.text =
//                if(testShowEnable) {
//                    "${LoginActivity.REMOTE_KEY_WELCOME_MSG} : ${welcomeMsg.toString()}" +
//                            "\n\n" +
//                            "${LoginActivity.REMOTE_KEY_APP_VERSION} : ${appVersion.toString()}" +
//                            "\n\n" +
//                            "${LoginActivity.REMOTE_KEY_TEST_SHOW_ENABLE} : ${testShowEnable.toString()}" +
//                            "\n\n" +
//                            "${LoginActivity.REMOTE_KEY_TEST_TITLE} : $testTitle" +
//                            "\n\n" +
//                            "${LoginActivity.REMOTE_KEY_TEST_MSG} : $testMsg"
//                } else {
//                    "${LoginActivity.REMOTE_KEY_TEST_SHOW_ENABLE} : ${testShowEnable.toString()}"
//                }
//
//        }
//
//        // button show dialog 클릭 시
//        binding.btnShowDialog.setOnClickListener {
//
//            val dialogInfoJson = JSONObject(dialogInfo)  //dialogInfo 를 JSONObject 로 변환
//
//            if(dialogInfoJson.getBoolean("show_enable")) {
//
//                val builder = AlertDialog.Builder(this)
//                val dialog = builder.setTitle("[${this.getString(R.string.app_name)}] ${dialogInfoJson.get("dialog_title")}")
//                    .setMessage("${dialogInfoJson.get("dialog_msg")}")
//                    .setNegativeButton("닫기", null)
//                    .setCancelable(false)
//                    .create()
//                dialog.show()
//
//            } else {
//                Toast.makeText(this, "Dialog Info - show_enable :: false", Toast.LENGTH_SHORT).show()
//            }
//
//        }
//
//    }


}