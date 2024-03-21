package com.bingyan.bbhust.ui.widgets

import android.text.style.BackgroundColorSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bingyan.bbhust.R
import com.bingyan.bbhust.base.TriState
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.lightThemeColor
import com.bingyan.bbhust.ui.theme.secondaryColor
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.ui.theme.white
import com.bingyan.bbhust.utils.ifElse
import com.bingyan.bbhust.utils.keep
import kotlinx.coroutines.delay

class AppLoading()
{

    private val showLoading = mutableStateOf(
        ShowLoading(
            msg = "",
            onDismiss = {},
            content = {}
        )
    )

    fun setState (
        msg: String,
        onDismiss: () -> Unit,
        timeout: Int = 0,
        dismissOnBackPress: Boolean = true,
        dismissOnClickOutside: Boolean = false,
        content: @Composable () -> Unit
    ): AppLoading{
        showLoading.value =
            ShowLoading(
                msg = msg,
                timeout = timeout,
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                onDismiss =onDismiss,
                content = content,
            )
        return this
    }

    fun show(show: Boolean):AppLoading{
        showLoading.value.show.value = show
        return this
    }

    @Composable
    fun Build(
    ) {
        val state = showLoading.value
        val showCancel = keep(v = false)
        LaunchedEffect(state.show, state.msg) {
            showCancel.value = false
            if (state.show.value && state.timeout > 0) {
                delay(state.timeout.toLong())
                showCancel.value = true
            }
        }
            if (state.show.value)
                Box(modifier = Modifier.fillMaxSize()
                    .background(Color.Blue.copy(0.0f))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {},
                            onDoubleTap = {},
                            onLongPress = {}
                        )
                    })
                {
                    Box(modifier = Modifier
                        .align(Alignment.Center)
                    ) {
                        Loading(
                            msg = state.msg,
                            showCancel = showCancel.value,
                            cancel = stringResource(id = R.string.cancel)
                        ) {
                            state.onDismiss()
                        }
                    state.content()
                    }
                }
//                Dialog(
//                    onDismissRequest = state.onDismiss,
//                    properties = DialogProperties(
//                        dismissOnBackPress = state.dismissOnBackPress,
//                        dismissOnClickOutside = state.dismissOnClickOutside
//                    )
//                ) {
//                    Box(
//                        Modifier
//                            .fillMaxSize()
//                            .background(color = Color.White)
//                    ) {
//                        Loading(
//                            msg = state.msg,
//                            showCancel = showCancel.value,
//                            cancel = stringResource(id = R.string.cancel)
//                        ) {
//                            state.onDismiss()
//                        }
//                    }
//                    state.content()
//                }

    }



    @Composable
    private fun Loading(msg: String, showCancel: Boolean, cancel: String, onClick: () -> Unit) {
        Column(
            Modifier
                .clip(CardShapes.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingBar(
                Modifier
                    .padding( 5.dp)
            )
//            Text(
//                text = msg,
//                fontSize = 13.sp,
//                fontWeight = FontWeight.Normal,
//                color = Color.DarkGray,
//                modifier = Modifier.padding(top = 16.dp)
//            )
//            Text(
//                text = cancel,
//                fontSize = 13.sp,
//                color = Color.Black,
//                modifier = Modifier
//                    .padding(bottom = 4.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .clickable(enabled = showCancel) { onClick() }
//                    .padding(horizontal = 8.dp, vertical = 2.dp)
//                    .alpha(showCancel.ifElse(1f, 0f))
//            )
        }
    }

    @Composable
    fun LoadingBar(modifier: Modifier = Modifier) {
        CircularProgressIndicator(
            modifier = modifier
                .size(35.dp),
            strokeWidth = 4.dp,
            color = lightThemeColor,
            trackColor = secondaryColor
        )
    }

    data class ShowLoading(
        val show: MutableState<Boolean> = mutableStateOf(false),
        val msg: String,
        val onDismiss: () -> Unit,
        val timeout: Int = 0,
        val dismissOnBackPress: Boolean = true,
        val dismissOnClickOutside: Boolean = false,
        val content: @Composable () -> Unit
    )
}