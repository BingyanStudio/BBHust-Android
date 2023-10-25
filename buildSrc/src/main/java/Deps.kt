import org.gradle.api.artifacts.dsl.DependencyHandler
object Accompanist {
    const val systemUiController =
        "com.google.accompanist:accompanist-systemuicontroller:0.31.1-alpha"
    const val navigationAnimation =
        "com.google.accompanist:accompanist-navigation-animation:0.31.2-alpha"
    const val reorderable = "org.burnoutcrew.composereorderable:reorderable:0.9.6"
}
object Coil {
    const val base = "io.coil-kt:coil:2.3.0"
    const val gif = "io.coil-kt:coil-gif:2.2.1"
    const val svg = "io.coil-kt:coil-svg:2.2.1"
    const val compose = "io.coil-kt:coil-compose:2.3.0"
}
object Compose {
    const val compilerVersion = "1.5.3"
    const val base = "androidx.activity:activity-compose:1.7.1"
    fun DependencyHandler.composeBom() = platform("androidx.compose:compose-bom:2023.10.01")
    const val ui = "androidx.compose.ui:ui"
    const val graphics = "androidx.compose.ui:ui-graphics"
    const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val material3 = "androidx.compose.material3:material3"
    const val material = "androidx.compose.material:material"
    const val constraint = "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha08"
    private const val nav_version = "2.5.3"
    const val navigation = "androidx.navigation:navigation-compose:$nav_version"
    const val junit = "androidx.compose.ui:ui-test-junit4"
    const val uiTooling = "androidx.compose.ui:ui-tooling"
    const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
}

object Kotlin {
    const val version = "1.9.10"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    const val kspVersion = "1.9.10-1.0.13"
}

object Lifecycle {
    private const val lifecycle_version = "2.5.1"
    const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"
    const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
    const val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"

}

object Markdown {
    private const val org = "org.commonmark"
    private const val ver = "0.21.0"
    private val mark = { name: String -> "$org:$name:$ver" }
    val commonMark = mark("commonmark")
    val gfmTable = mark("commonmark-ext-gfm-tables")
    val taskList = mark("commonmark-ext-task-list-items")
    val strikethough = mark("commonmark-ext-gfm-strikethrough")
    val autolink = mark("commonmark-ext-autolink")
}

object Moshi {
    private const val version = "1.14.0"
    const val base = "com.squareup.moshi:moshi:$version"
    const val kotlin = "com.squareup.moshi:moshi-kotlin:$version"
    const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:1.14.0"
}

object PushService {
    //个推 推送服务,未上架暂时无法接入小米
    const val sdk = "com.getui:gtsdk:3.2.12.0"  //个推SDK
    const val core = "com.getui:gtc:3.1.10.0"  //个推核心组件
    const val huawei = "com.getui.opt:hwp:3.1.1"   // 华为
    const val hms = "com.huawei.hms:push:6.5.0.300"
    const val oppo = "com.assist-v3:oppo:3.1.2"  // oppo
    const val vivo = "com.assist-v3:vivo:3.1.0"  // vivo
    const val meizu = "com.getui.opt:mzp:3.2.2"   // 魅族
}

object Retrofit {
    const val base = "com.squareup.retrofit2:retrofit:2.9.0"
    const val converter = "com.squareup.retrofit2:converter-moshi:2.9.0"
}

object TestTool {
    const val junit = "junit:junit:4.13.2"
    const val junitExt = "androidx.test.ext:junit:1.1.5"
    const val espresso = "androidx.test.espresso:espresso-core:3.5.1"
}

object Utils {
    const val androidUtils = "com.blankj:utilcodex:1.31.1"
    const val jsoup = "org.jsoup:jsoup:1.16.1"
    const val palette = "androidx.palette:palette-ktx:1.0.0"
    const val gridPad = "com.touchlane:gridpad:1.1.0"
    const val tooltip = "com.github.skgmn:composetooltip:0.2.0"
    const val zxing = "com.google.zxing:core:3.5.0"
    const val nestScrollView = "com.github.Tlaster:NestedScrollView:0.7.0"
    const val apollo = "com.apollographql.apollo3:apollo-runtime:3.8.1"
    const val androidxCore = "androidx.core:core-ktx:1.10.1"
    const val material = "com.google.android.material:material:1.9.0"
    const val mmkv = "com.tencent:mmkv:1.2.16"
    const val webkit = "androidx.webkit:webkit:1.8.0"
}

object Umeng {
    const val sdk = "com.umeng.umsdk:common:9.5.1" // (必选)版本号
    const val asms = "com.umeng.umsdk:asms:1.6.3" // asms包依赖(必选)
    const val apm = "com.umeng.umsdk:apm:1.7.0"//错误分析
}

object Version {
    const val versionCode = 1
    const val versionName = "1.0"
}