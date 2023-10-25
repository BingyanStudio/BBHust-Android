package com.bingyan.bbhust.ui.screen.browser

import android.webkit.JavascriptInterface
import androidx.navigation.NavHostController
import com.bingyan.bbhust.ui.provider.login
import com.bingyan.bbhust.utils.AuthorUtils

open class JsBridge(
    val onNav: ((NavHostController) -> Unit) -> Unit
) {
    @JavascriptInterface
    open fun login(
        token: String,
        permission: String,
        id: String
    ) {
        AuthorUtils.token = token
        AuthorUtils.personID = id
        AuthorUtils.permission = permission == "1"
        onNav {
            it.popBackStack()
        }
    }

    @JavascriptInterface
    fun logout() {
        onNav {
            AuthorUtils.logout()
            it.login()
        }
    }
}
