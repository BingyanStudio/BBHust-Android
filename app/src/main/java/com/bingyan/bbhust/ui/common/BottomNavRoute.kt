package com.bingyan.bbhust.ui.common

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.bingyan.bbhust.R


sealed class BottomNavRoute(
    var routeNumber: Int,
    @StringRes var stringId: Int,
    @DrawableRes var icon: Int,
    @DrawableRes var iconSelected: Int = icon
) {
    data object Home : BottomNavRoute(
        Route.Home,
        R.string.home,
        R.drawable.bottom_home,
    )

    data object Tags :
        BottomNavRoute(
            Route.Tags,
            R.string.tag,
            R.drawable.bottom_tag,
        )

    data object Notification :
        BottomNavRoute(
            Route.Notification,
            R.string.message,
            R.drawable.bottom_notification,
        )

    data object Me :
        BottomNavRoute(
            Route.Me,
            R.string.me,
            R.drawable.bottom_me
        )

    data object Add :
        BottomNavRoute(Route.Add, R.string.create_post, R.drawable.bottom_add)
}

object Route {
    const val Home = 0
    const val Tags = 1
    const val Notification = 2
    const val Me = 3
    const val Add = -1
}