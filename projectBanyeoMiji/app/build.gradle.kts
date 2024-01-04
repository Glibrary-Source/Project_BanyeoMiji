import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

android {
    namespace = "com.twproject.banyeomiji"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.twproject.banyeomiji"
        minSdk = 24
        targetSdk = 33
        versionCode = 10
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

        manifestPlaceholders["NAVER_MAP_API"] = localProperties["NAVER_MAP_API"].toString()
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "NAVER_CLIENT_ID", "" + localProperties["NAVER_CLIENT_ID"] + "")
            buildConfigField("String", "NAVER_CLIENT_SECRET", "" + localProperties["NAVER_CLIENT_SECRET"] + "")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "NAVER_CLIENT_ID", "" + localProperties["NAVER_CLIENT_ID"] + "")
            buildConfigField("String", "NAVER_CLIENT_SECRET", "" + localProperties["NAVER_CLIENT_SECRET"] + "")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val navVersion = "2.5.3"
    val billing_version = "6.1.0"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:32.2.3"))
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation("com.google.firebase:firebase-storage:20.2.1")

    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.naver.maps:map-sdk:3.17.0")

    implementation("com.navercorp.nid:oauth:5.8.0") // jdk 11

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.android.gms:play-services-ads:21.5.0")

    implementation("com.google.android.gms:play-services-auth:20.2.0")
    implementation("com.google.firebase:firebase-auth-ktx:21.0.4")

    implementation("com.airbnb.android:lottie:3.1.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.6.4")

    implementation("io.github.vaneproject:badwordfiltering:1.0.0")

    implementation("com.tbuonomo:dotsindicator:5.0")

    // google pay
    implementation("com.android.billingclient:billing-ktx:$billing_version")
}