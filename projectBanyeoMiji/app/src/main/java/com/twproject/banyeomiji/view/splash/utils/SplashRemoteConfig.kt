package com.twproject.banyeomiji.view.splash.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.main.MainActivity
import com.twproject.banyeomiji.view.splash.SplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

class SplashRemoteConfig(
    private val context: Context
) {

    companion object {
        const val REMOTE_KEY_DIALOG_INFO = "dialog_info"
        const val REMOTE_KEY_UPDATE_EMERGENCY = "update_emergency"
        const val REMOTE_KEY_UPDATE_NORMAL = "update_normal"
    }

    private val activity = context as SplashActivity
    private var dialogInfo: String? = null
    private var updateEmergency: Boolean = false
    private var updateNormal: Boolean = false
    fun initRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig  //Remote Config 객체 가져오기
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            mapOf(
                //default 값 설정
                REMOTE_KEY_UPDATE_EMERGENCY to false,
                REMOTE_KEY_UPDATE_NORMAL to false
            )
        }

        remoteConfig.setConfigSettingsAsync(configSettings)  //객체 default 값 설정 - map 형태

        //값 가져 오기 및 활성화
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    //매개 변수 KEY 값 설정
                    updateEmergency = remoteConfig.getBoolean(REMOTE_KEY_UPDATE_EMERGENCY)
                    updateNormal = remoteConfig.getBoolean(REMOTE_KEY_UPDATE_NORMAL)
                    dialogInfo = remoteConfig.getString(REMOTE_KEY_DIALOG_INFO)

                    if (updateEmergency) {
                        emergencyAlert(dialogInfo)
                    } else if (updateNormal) {
                        normalAlert()
                    } else {
                        startMain()
                    }
                } else {
                    Toast.makeText(
                        context, "실패",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun normalAlert() {

//        val dialogInfoJson = JSONObject(dialogInfo)//나중 삭제 필요없음 Json 형태 보려고 남겨둔것

        val builder = AlertDialog.Builder(context)
        val dialog =
            builder.setTitle("[${context.getString(R.string.app_name)}] 공지사항")
                .setMessage("업데이트를 해주세요")
                .setNegativeButton("닫기")
                { _, _ ->
                    startMain()
                }
                .setPositiveButton("연결")
                { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data =
                        Uri.parse("https://play.google.com/store/apps/details?id=com.kapitalletter.wardoffice")
                    activity.startActivity(intent)
                    activity.finish()
                }
                .setCancelable(false)
                .create()
        dialog.show()
    }

    private fun emergencyAlert(dialogInfo: String?) {

//        val dialogInfoJson = JSONObject(dialogInfo)

        val builder = AlertDialog.Builder(context)
        val dialog =
            builder.setTitle("[${context.getString(R.string.app_name)}] 공지사항")
                .setMessage("업데이트를 해주세요")
                .setNegativeButton("연결")
                { _, _ ->
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.kapitalletter.wardoffice")
                    activity.startActivity(intent)
                    activity.finish()
                }
                .setCancelable(false)
                .create()
        dialog.show()
    }

    private fun startMain() {
        val intent = Intent(activity, MainActivity::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000)
            activity.startActivity(intent)
            activity.finish()
        }
    }

}