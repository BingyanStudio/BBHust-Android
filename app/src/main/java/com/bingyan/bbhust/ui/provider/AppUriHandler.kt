package com.bingyan.bbhust.ui.provider

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.bingyan.bbhust.App
import com.blankj.utilcode.util.ClipboardUtils
import java.net.URISyntaxException

class AppUriHandler(private val nav: NavHostController, private val activity: Activity) :
    UriHandler {
    override fun openUri(uri: String) {
        if (uri.startsWith("intent://")) {
            val intent: Intent
            try {
                intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME)
                intent.addCategory("android.intent.category.BROWSABLE")
                intent.component = null
                intent.selector = null
                val resolves: List<ResolveInfo> =
                    activity.packageManager.queryIntentActivities(intent, 0)
                if (resolves.isNotEmpty()) {
                    activity.startActivityIfNeeded(intent, -1)
                }
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        } else {
            try {
                nav.navigate(uri.toUri())
            } catch (_: Exception) {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity.startActivity(intent)
                } catch (e: Exception) {
                    ClipboardUtils.copyText(uri)
                    Toast.makeText(App.CONTEXT, e.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}