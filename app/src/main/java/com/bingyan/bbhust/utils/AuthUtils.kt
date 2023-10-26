package com.bingyan.bbhust.utils

import com.bingyan.bbhust.App
import com.igexin.sdk.PushManager

object AuthUtils {
    var personID: String?
        get() = SP.get(SpUser.PERSON_ID, null)
        set(personID) {
            SP.set(SpUser.PERSON_ID, personID)
        }
    var token: String?
        get() = SP.get(SpUser.PERSON_TOKEN, null)
        set(token) {
            SP.set(SpUser.PERSON_TOKEN, token)
        }
    var permission: Boolean
        get() = SP.get(SpUser.PERMISSION, null) == "1"
        set(v) = if (v) SP.set(SpUser.PERMISSION, "1") else SP.remove(SpUser.PERMISSION)

    fun logout() {
        PushManager.getInstance()
            .unBindAlias(App.CONTEXT, personID, true)
        personID = null
        token = null
        permission = false
    }
}