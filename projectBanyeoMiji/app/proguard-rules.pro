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
-keepclassmembers class com.twproject.banyeomiji.view.main.datamodel.PetLocationData
-keepclassmembers class com.twproject.banyeomiji.view.login.datamodel.UserDataModel

-keep class com.naver.maps.map.** {*; }
-keep class com.naver.maps.geometry.** {*; }
-keep class com.naver.maps.map.LocationTrackingMode
-keep class com.naver.maps.map.MapFragment
-keep class com.naver.maps.map.NaverMap
-keep class com.naver.maps.map.OnMapReadyCallback
-keep class com.naver.maps.map.util.FusedLocationSource
-keepclassmembers class com.naver.maps.map.LocationTrackingMode
-keepclassmembers class com.naver.maps.map.MapFragment
-keepclassmembers class com.naver.maps.map.NaverMap
-keepclassmembers class com.naver.maps.map.OnMapReadyCallback
-keepclassmembers class com.naver.maps.map.util.FusedLocationSource

-dontwarn com.naver.maps.map.LocationTrackingMode
-dontwarn com.naver.maps.map.MapFragment
-dontwarn com.naver.maps.map.NaverMap
-dontwarn com.naver.maps.map.OnMapReadyCallback
-dontwarn com.naver.maps.map.util.FusedLocationSource