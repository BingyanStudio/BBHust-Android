package com.bingyan.bbhust.ui.screen.login

import com.bingyan.bbhust.base.BaseViewModel
import com.umeng.analytics.MobclickAgent


class LoginViewModel : BaseViewModel<Unit, Unit>(Unit) {
    init {
        MobclickAgent.onProfileSignOff()
    }

    override fun reduce(action: Unit) {
    }
}