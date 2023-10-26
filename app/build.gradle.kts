plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.apollographql.apollo3")
    id("com.google.devtools.ksp")
}

apollo {
    service("srv") {
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
        manifestPlaceholders.putAll(
            mapOf(
                "GETUI_APPID" to "q5u4D40bLn8xOHZMZPnOq2",
                // 华为 相关应用参数
                "HUAWEI_APP_ID" to "107164745",
                // OPPO 相关应用参数
                "OPPO_APP_KEY" to "a39608c7bbbf4598bcbfe7b04c5f46d2",
                "OPPO_APP_SECRET" to "2e26edcf19a94e6f8c6e35457712bf83",
                // VIVO 相关应用参数
                "VIVO_APP_ID" to "105596268",
                "VIVO_APP_KEY" to "42463640abd6fdc90d06386342f4d16c",
                // 魅族相关应用参数
                "MEIZU_APP_ID" to "150176",
                "MEIZU_APP_KEY" to "22fdb3e7b3914e2c9db878d852e22a0c",
            )
        )
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            resValue("string", "app_name", "@string/app_name_stable")
            setProguardFiles(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            )
        }
        debug {
            applicationIdSuffix = ".dev"
            resValue("string", "app_name", "@string/app_name_dev")
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
    implementation(project(":richtext"))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    Accompanist.apply {
        implementation(reorderable)
        implementation(systemUiController)
        implementation(navigationAnimation)
    }

    Coil.apply {
        implementation(base)
        implementation(gif)
        implementation(svg)
        implementation(compose)
    }

    Compose.apply {
        implementation(base)
        implementation(composeBom())
        implementation(ui)
        implementation(graphics)
        implementation(uiToolingPreview)
        implementation(material)
        implementation(constraint)
        implementation(material3)
        implementation(navigation)

        androidTestImplementation(composeBom())
        androidTestImplementation(junit)
        debugImplementation(uiTooling)
        debugImplementation(uiTestManifest)
    }

    Lifecycle.apply {
        implementation(runtime)
        implementation(viewModel)
        implementation(viewModelCompose)
        implementation(liveData)
        implementation(savedState)
    }

    Markdown.apply {
        implementation(commonMark)
        implementation(autolink)
        implementation(strikethough)
        implementation(gfmTable)
        implementation(taskList)
    }

    Moshi.apply {
        implementation(base)
        implementation(kotlin)
        ksp(codegen)
    }

    PushService.apply {
        implementation(sdk)
        implementation(core)
        implementation(huawei)
        implementation(hms)
        implementation(oppo)
        implementation(vivo)
        implementation(meizu)
    }

    Retrofit.apply {
        implementation(base)
        implementation(converter)
    }

    Utils.apply {
        implementation(gridPad)
        implementation(palette)
        implementation(tooltip)
        implementation(zxing)
        implementation(nestScrollView)
        implementation(androidUtils)
        implementation(jsoup)
        implementation(apollo)
        implementation(androidxCore)
        implementation(material)
        implementation(mmkv)
        implementation(webkit)
    }

    Umeng.apply {
        implementation(sdk)
        implementation(asms)
        implementation(apm)
    }

    TestTool.apply {
        testImplementation(junit)
        androidTestImplementation(junitExt)
        androidTestImplementation(espresso)
    }
}

task("generateApollo") {
    dependsOn("generateServiceApolloSources")
}