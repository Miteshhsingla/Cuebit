plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cuebit.io"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cuebit.io"
        minSdk = 24
        targetSdk = 34
        versionCode = 4
        versionName = "1.1.1"
        multiDexEnabled = true

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging{
        jniLibs {
            pickFirsts += listOf(
                "lib/x86_64/libjsc.so",
                "lib/arm64-v8a/libjsc.so",
                "lib/x86/libc++_shared.so",
                "lib/arm64-v8a/libc++_shared.so",
                "lib/x86_64/libc++_shared.so",
                "lib/armeabi-v7a/libc++_shared.so"
            )
        }
        resources {
            excludes += listOf(
                "**/module-info.class",
                "META-INF/DEPENDENCIES.txt",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/DEPENDENCIES",
                "META-INF/notice.txt",
                "META-INF/license.txt",
                "META-INF/dependencies.txt",
                "META-INF/LGPL2.1",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module"
            )
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = "key"
            keyPassword = "android"
            storePassword = "android"
            storeFile = file("keystore/keyStore.jks")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFile("proguard-rules.pro")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            lint{
                lint.abortOnError = false
                lint.checkReleaseBuilds = false
                lint.warningsAsErrors = false
                lint.disable += "UnusedResources"
                lint.disable += "MissingTranslation"
            }
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false
            isDebuggable = true
            isShrinkResources = false
            proguardFile("proguard-rules.pro")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            lint{
                lint.abortOnError = false
                lint.checkReleaseBuilds = false
                lint.warningsAsErrors = false
                lint.disable += "UnusedResources"
                lint.disable += "MissingTranslation"
            }
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.multidex:multidex:2.0.1")
//    implementation ("com.google.android.play:review:2.0.1")
//    implementation ("com.google.android.play:app-update:2.1.0")
}