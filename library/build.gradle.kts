plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  namespace = "miuix.path"
  compileSdk = 36

  defaultConfig {
    minSdk = 21
  }
  
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  // Core Android dependencies
  implementation("androidx.annotation:annotation:1.7.0")
}