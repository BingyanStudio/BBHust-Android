package com.bingyan.bbhust.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.themeColor

@Composable
fun AppCheckBox(modifier: Modifier = Modifier, checked: Boolean, onChanged: (Boolean) -> Unit) {
    IconButton(onClick = { onChanged(!checked) }) {
        Image(
            modifier = modifier
                .border(2.dp, themeColor, CircleShape)
                .clip(CircleShape)
                .background(if (checked) themeColor else Color.Transparent)
                .padding(2.dp)
                .size(16.dp),
            painter = painterResource(id = R.drawable.check), contentDescription = null,
            colorFilter = ColorFilter.tint(if (checked) Color.White else Color.Transparent)
        )
    }
}

@Preview
@Composable
fun AppCheckBoxPreview() {
    val checked = remember {
        mutableStateOf(false)
    }
    AppCheckBox(checked = checked.value) {
        checked.value = !checked.value
    }
}