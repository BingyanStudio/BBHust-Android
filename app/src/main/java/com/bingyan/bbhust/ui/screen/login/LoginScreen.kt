@file:OptIn(ExperimentalAnimationApi::class)

package com.bingyan.bbhust.ui.screen.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bingyan.bbhust.App
import com.bingyan.bbhust.AppRoute
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.provider.LocalNav
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.ui.widgets.FillButton
import com.bingyan.bbhust.utils.AuthUtils
import com.bingyan.bbhust.utils.string
import com.umeng.analytics.MobclickAgent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var isExit = false

object LoginScreen {
    @Composable
    fun View() {
        LaunchedEffect(Unit) {
            MobclickAgent.onProfileSignOff() // 友盟统计退出统计
        }
        val nav = LocalNav.current
        val scope = rememberCoroutineScope()
        BackHandler {
            if (!isExit && nav.currentBackStackEntry?.destination?.route == AppRoute.MAIN) {
                scope.launch {
                    isExit = true
                    Toast.makeText(
                        App.CONTEXT, R.string.press_again.string, Toast.LENGTH_SHORT
                    ).show()
                    delay(2000)
                    isExit = false
                }
            } else {
                nav.popBackStack()
            }
        }
        // 登录结果监听
        DisposableEffect(Unit) {
            val listener =
                NavController.OnDestinationChangedListener { controller, destination, arguments ->
                    Log.i("LoginScreen", "Dest: $destination")
                    if (destination.route == AppRoute.LOGIN) {
                        if (AuthUtils.token != null) {
                            nav.navigate(
                                AppRoute.MAIN, NavOptions.Builder().apply {
                                    this.setPopUpTo(
                                        AppRoute.LOGIN,
                                        inclusive = true
                                    )
                                }.build()
                            )
                        }
                    }
                }
            nav.addOnDestinationChangedListener(listener)
            onDispose {
                nav.removeOnDestinationChangedListener(listener)
            }
        }
        Surface {
            Column(
                Modifier
                    .background(colors.background)
                    .imePadding()
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier.size(ImageSize.XLarge),
                        painter = painterResource(id = R.mipmap.ic_launcher),
                        contentDescription = stringResource(id = R.string.app_name)
                    )
                    Spacer(modifier = Modifier.height(Gap.Big))
                    Text(
                        text = string(R.string.welcome),
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W700
                    )
                }
                Spacer(modifier = Modifier.height(Gap.Big))
                Spacer(
                    modifier = Modifier
                        .weight(2f)
                )
                //登录按钮
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FillButton(
                        modifier = Modifier
                            .padding(horizontal = Gap.Big * 4)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.login),
                        color = themeColor,
                        textColor = Color.White
                    ) {
                        nav.navigate(AppRoute.H5.login)
                    }
                    Spacer(modifier = Modifier.height(Gap.Big))
                    //注册按钮
                    FillButton(
                        modifier = Modifier
                            .padding(horizontal = Gap.Big * 4)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.register),
                        color = colors.card,
                        textColor = themeColor
                    ) {
                        nav.navigate(AppRoute.H5.register)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

}