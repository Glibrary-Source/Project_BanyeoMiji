# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.twproject.banyeomiji.view.main.datamodel.** {*; }
-keep class com.twproject.banyeomiji.view.login.datamodel.** {*; }
-keep class com.naver.** {*; }
-keep class com.naver.maps.map.** {*; }
-keep class com.naver.maps.geometry.** {*; }

#-keep class com.naver.maps.map.a.** {*; }
#-keep class com.naver.maps.map.app.** {*; }
#-keep class com.naver.maps.map.indoor.** {*; }
#-keep class com.naver.maps.map.internal.** {*; }
#-keep class com.naver.maps.map.log.** {*; }
#-keep class com.naver.maps.map.offline.** {*; }
#-keep class com.naver.maps.map.overlay.** {*; }
#-keep class com.naver.maps.map.renderer.** {*; }
#-keep class com.naver.maps.map.snapshotter.** {*; }
#-keep class com.naver.maps.map.style.** {*; }
#-keep class com.naver.maps.map.text.** {*; }
#-keep class com.naver.maps.map.util.** {*; }
#-keep class com.naver.maps.map.widget.** {*; }
-keepclassmembers class com.twproject.banyeomiji.view.main.datamodel.PetLocationData
-keepclassmembers class com.twproject.banyeomiji.view.login.datamodel.UserDataModel