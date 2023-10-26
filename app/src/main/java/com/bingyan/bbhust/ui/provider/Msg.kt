package com.bingyan.bbhust.ui.provider

import android.app.Activity
import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.bingyan.bbhust.AppRoute
import com.bingyan.bbhust.R
import com.bingyan.bbhust.utils.string
import com.blankj.utilcode.util.ClipboardUtils

fun NavHostController.jump(dest: String) {
    navigate(dest)
}

fun NavHostController.browseInternal(dest: String) {
    jump(AppRoute.BROWSER + "?url=" + dest)
}

fun NavHostController.jumpOption(dest: String, option: NavOptions) {
    navigate(dest, option)
}

fun NavHostController.login() {
    jumpOption(AppRoute.LOGIN, navOptions {
        popUpTo(AppRoute.MAIN) {
            inclusive = true
        }
    })
}

fun Activity.jumpIntent(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        toast(R.string.app_not_installed.string)
    }
}

fun Activity.browseExternal(url: String) {
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    } catch (e: Exception) {
        ClipboardUtils.copyText(url)
        toast(string(R.string.no_app))
    }
}

fun Activity.jumpClass(cls: Class<*>) {
    try {
        startActivity(
            Intent(this, cls),
            ActivityOptions.makeSceneTransitionAnimation(this)
                .toBundle()
        )
    } catch (e: ActivityNotFoundException) {
        toast(R.string.app_not_installed.string)
    }
}

@Composable
fun Pop() {
    val nav = LocalNav.current
    nav.popBackStack()
}

fun Activity.toast(msg: String) {
    runOnUiThread {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}