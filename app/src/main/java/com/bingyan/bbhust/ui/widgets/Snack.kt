package com.bingyan.bbhust.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bingyan.bbhust.ui.theme.Gap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class Snack
{
    private val snack_ = MutableStateFlow(SnackData(""))
    private val snackFlow get() = snack_.asSharedFlow()
    private val hostState = SnackHostState()
    @Composable
    fun Build(
        content:@Composable ()->Unit
    ){
        content()
        LaunchedEffect(key1 = Unit) {
            snackFlow.collect {
                if (it.message.isNotBlank()) {
                    hostState.showSnackbar(it.message, it.duration, it.level)
                    snack("")
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = Gap.Big * 5),
            contentAlignment = Alignment.BottomCenter
        ) {
            SnackbarHost(hostState = hostState) {
                AppSnackBar(data = it)
            }
        }
    }

    fun snack(
        str: String,
        level: SnackLevel = SnackLevel.Success,
        duration: SnackDuration = SnackDuration.Short,
    ) {
        snack_.value = SnackData(
            str, duration, level
        )
    }

}