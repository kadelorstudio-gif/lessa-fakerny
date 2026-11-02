import java.util.Base64

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.plugin.compose")
  id("com.google.devtools.ksp")
  id("com.google.gms.google-services")
  alias(libs.plugins.secrets)
}

// Auto-generate fallback google-services.json if missing so CI/CD and external builds succeed seamlessly
val googleServicesConfigFile = file("google-services.json")
if (!googleServicesConfigFile.exists()) {
  try {
    googleServicesConfigFile.writeText(
      """{
  "project_info": {
    "project_number": "247548670509",
    "project_id": "lessa-fakerny",
    "storage_bucket": "lessa-fakerny.firebasestorage.app"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:247548670509:android:f40b3204e470fa17283ea4",
        "android_client_info": {
          "package_name": "app.lessafakerny.android"
        }
      },
      "oauth_client": [
        {
          "client_id": "247548670509-7flpquvd9l60pc1jvrcjpmgemadbba1h.apps.googleusercontent.com",
          "client_type": 3
        }
      ],
      "api_key": [
        {
          "current_key": "AIzaSyAWkpT24SASeuUgHJYmMv9j-itF7JNuceU"
        }
      ],
      "services": {}
    }
  ],
  "configuration_version": "1"
}"""
    )
  } catch (e: Exception) {
    // ignore
  }
}

android {
  namespace = "app.lessafakerny.android"
  compileSdk = 36

  defaultConfig {
    applicationId = "app.lessafakerny.android"
    minSdk = 24
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    val webClientId = try {
      val gsFile = file("google-services.json")
      if (gsFile.exists()) {
        val jsonText = gsFile.readText()
        val match = Regex("\"client_type\"\\s*:\\s*3[\\s\\S]*?\"client_id\"\\s*:\\s*\"([^\"]+)\"").find(jsonText)
          ?: Regex("\"client_id\"\\s*:\\s*\"([^\"]+)\"[\\s\\S]*?\"client_type\"\\s*:\\s*3").find(jsonText)
        match?.groupValues?.get(1) ?: "247548670509-7flpquvd9l60pc1jvrcjpmgemadbba1h.apps.googleusercontent.com"
      } else {
        "247548670509-7flpquvd9l60pc1jvrcjpmgemadbba1h.apps.googleusercontent.com"
      }
    } catch (e: Exception) {
      "247548670509-7flpquvd9l60pc1jvrcjpmgemadbba1h.apps.googleusercontent.com"
    }

    buildConfigField("String", "DEFAULT_WEB_CLIENT_ID", "\"$webClientId\"")
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      val ksFile = file("${rootDir}/debug.keystore")
      if (!ksFile.exists()) {
        val base64File = file("${rootDir}/debug.keystore.base64")
        if (base64File.exists()) {
          try {
            val bytes = Base64.getDecoder().decode(base64File.readText().trim())
            ksFile.writeBytes(bytes)
          } catch (e: Exception) {
            // ignore
          }
        }
      }
      val userHomeKeystore = file("${System.getProperty("user.home")}/.android/debug.keystore")
      storeFile = if (ksFile.exists()) ksFile else userHomeKeystore
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Use custom debugConfig if local keystore file exists, otherwise standard debug signing managed by AGP
      val customKs = file("${rootDir}/debug.keystore")
      signingConfig = if (customKs.exists()) {
        signingConfigs.getByName("debugConfig")
      } else {
        signingConfigs.getByName("debug")
      }
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.firestore)

  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.play.services.ads)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
}
