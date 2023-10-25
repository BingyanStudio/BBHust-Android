package com.bingyan.bbhust.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.utils.ifElse
import com.bingyan.bbhust.utils.keep
import kotlinx.coroutines.delay

@Composable
fun Loading(
    show: Boolean,
    msg: String,
    onDismiss: () -> Unit,
    timeout: Int = 0,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = false,
    content: @Composable () -> Unit
) {
    val showCancel = keep(v = false)
    LaunchedEffect(show, msg) {
        showCancel.value = false
        if (show && timeout > 0) {
            delay(timeout.toLong())
            showCancel.value = true
        }
    }
    Box {
        if (show)
            Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(
                    dismissOnBackPress = dismissOnBackPress,
                    dismissOnClickOutside = dismissOnClickOutside
                )
            ) {
                Loading(
                    msg = msg,
                    showCancel = showCancel.value,
                    cancel = stringResource(id = R.string.cancel)
                ) {
                    onDismiss()
                }
            }
        content()
    }
}

@Composable
private fun Loading(msg: String, showCancel: Boolean, cancel: String, onClick: () -> Unit) {
    Column(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoadingBar(
            Modifier
                .padding(top = 32.dp)
        )
        Text(
            text = msg,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = cancel,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = showCancel) { onClick() }
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .alpha(showCancel.ifElse(1f, 0f))
        )
    }
}

@Composable
fun LoadingBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .size(40.dp),
        strokeWidth = 4.dp,
        color = themeColor
    )
}