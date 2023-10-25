import Compose.composeBom

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.apollographql.apollo3")
}

apollo {
    service("srv") {
//        packageName.set("com.example")
        packageNamesFromFilePaths()
        mapScalarToKotlinString("DateTime")
    }
}
android {
    namespace = "com.bingyan.bbhust"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bingyan.bbhust.nt"
        minSdk = 21
        targetSdk = 34
        versionCode = Version.versionCode
        versionName = Version.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Compose.compilerVersion
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
dependencies {
    implementation(Accompanist.reorderable)
    implementation(Utils.gridPad)
    implementation(Utils.palette)

    implementation(project(":richtext"))
    implementation(Markdown.commonMark)
    implementation(Markdown.autolink)
    implementation(Markdown.strikethough)
    implementation(Markdown.gfmTable)
    implementation(Markdown.taskList)
    implementation(WebKit.base)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(Utils.androidUtils)
    implementation(Utils.jsoup)

    implementation(MMKV.base)

    implementation(Apollo.apollo)

    implementation(Accompanist.systemUiController)
    implementation(Accompanist.navigationAnimation)

    implementation(Coil.base)
    implementation(Coil.compose)

    implementation(AndroidX.core)

    implementation(Google.material)

    implementation(Lifecycle.runtime)
    implementation(Lifecycle.viewModel)
    implementation(Lifecycle.viewModelCompose)
    implementation(Lifecycle.liveData)
    implementation(Lifecycle.savedState)

    implementation(Compose.base)
    implementation(composeBom())
    implementation(Compose.ui)
    implementation(Compose.graphics)
    implementation(Compose.uiToolingPreview)
    implementation(Compose.material)
    implementation(Compose.constraint)
    implementation(Compose.material3)
    implementation(Compose.navigation)

    testImplementation(Test.junit)
    androidTestImplementation(Test.junitExt)
    androidTestImplementation(Test.espresso)
    androidTestImplementation(composeBom())
    androidTestImplementation(Compose.junit)
    debugImplementation(Compose.uiTooling)
    debugImplementation(Compose.uiTestManifest)
}

task("generateApollo") {
    dependsOn("generateServiceApolloSources")
}