package com.twproject.banyeomiji.view.splash.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.twproject.banyeomiji.R
import com.twproject.banyeomiji.view.main.MainActivity
import com.twproject.banyeomiji.view.splash.SplashActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashRemoteConfig(
    private val context: Context
) {
    companion object {
        const val REMOTE_KEY_APP_VERSION = "app_version"
    }

    private val versionName = context.getPackageInfo().versionName
    private val activity = context as SplashActivity

    private var appVersion: String? = null
    fun initRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig  //Remote Config 객체 가져오기
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
            mapOf(
                //default 값 설정
                REMOTE_KEY_APP_VERSION to versionName
            )
        }

        remoteConfig.setConfigSettingsAsync(configSettings)  //객체 default 값 설정 - map 형태

        //값 가져 오기 및 활성화
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    //매개 변수 KEY 값 설정
                    appVersion = remoteConfig.getString(REMOTE_KEY_APP_VERSION)

                    if (appVersion == versionName) {
                        startMain()
                    } else {
                        showAlert()
                    }
                } else {
                    Toast.makeText(
                        context, "인터넷 연결을 확인해주세요",
                        Toast.LENGTH_SHORT
                    ).show()
                    CoroutineScope(Main).launch {
                        delay(2000)
                        activity.finish()
                    }
                }
            }
    }

    private fun showAlert() {
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
        CoroutineScope(Main).launch {
            delay(2000)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    @Suppress("DEPRECATION")
    private fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }

}