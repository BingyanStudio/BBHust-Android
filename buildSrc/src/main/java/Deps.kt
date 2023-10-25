import org.gradle.api.artifacts.dsl.DependencyHandler

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

object WebKit {
    private const val org = "androidx.webkit:webkit"
    private const val ver = "1.8.0"
    const val base = "$org:$ver"
}

object Version {
    const val versionCode = 1
    const val versionName = "1.0"
}

object Utils {
    const val androidUtils = "com.blankj:utilcodex:1.31.1"
    const val jsoup = "org.jsoup:jsoup:1.16.1"
    const val palette = "androidx.palette:palette-ktx:1.0.0"
    const val gridPad = "com.touchlane:gridpad:1.1.0"
}

object Apollo {
    const val apollo = "com.apollographql.apollo3:apollo-runtime:3.8.1"
}

object Accompanist {
    const val systemUiController =
        "com.google.accompanist:accompanist-systemuicontroller:0.31.1-alpha"
    const val navigationAnimation =
        "com.google.accompanist:accompanist-navigation-animation:0.31.2-alpha"
    const val reorderable = "org.burnoutcrew.composereorderable:reorderable:0.9.6"
}

object Moshi {
    const val base = "com.squareup.moshi:moshi:1.14.0"
    const val codegen = "com.squareup.moshi:moshi-kotlin-codegen:1.14.0"
}

object Coil {
    const val base = "io.coil-kt:coil:2.3.0"
    const val compose = "io.coil-kt:coil-compose:2.3.0"
}

object AndroidX {
    const val core = "androidx.core:core-ktx:1.10.1"
}

object Lifecycle {
    private const val lifecycle_version = "2.5.1"
    const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"
    const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"
    const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"
    const val savedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"

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

object Google {
    const val material = "com.google.android.material:material:1.9.0"
}

object Kotlin {
    const val version = "1.9.10"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
    const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
    const val kspVersion = "1.8.20-1.0.11"
}

object MMKV {
    private const val version = "1.2.16"
    const val base = "com.tencent:mmkv:$version"
}


object Test {
    const val junit = "junit:junit:4.13.2"
    const val junitExt = "androidx.test.ext:junit:1.1.5"
    const val espresso = "androidx.test.espresso:espresso-core:3.5.1"
}