# 开发手册

## 依赖版本管理

使用 buildSrc 进行统一的依赖版本管理，详见 [Deps.kt](../buildSrc/src/main/java/Deps.kt)。

### 更新 Compose 版本

> TIPS: Android 官网简体中文版本存在过时信息，请主动切换至 English Version 查询最新版本

1. 查询 [BOM 映射表](https://developer.android.com/jetpack/compose/bom/bom-mapping) ，选择最新的 BOM
   版本.

2. 更新 [Deps.kt](../buildSrc/src/main/java/Deps.kt) 中的 `composeBom()` 函数中的版本号。

### 更新 Kotlin 版本 & Compose Compiler 版本

1.
查询 [Compiler 与 Kotlin 版本对应表](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
选择合适的 Kotlin 版本和 Compiler 版本.

2. 更新 [Deps.kt](../buildSrc/src/main/java/Deps.kt) 中的 `Kotlin.version`
   和 `Compose.compilerVersion` 变量.

3. 受限于 Deps.kt 作用范围，你还需要

## App 版本管理

更新 [Deps.kt](../buildSrc/src/main/java/Deps.kt) 中的 `Version.versionCode`
和 `Version.versionName` 变量.