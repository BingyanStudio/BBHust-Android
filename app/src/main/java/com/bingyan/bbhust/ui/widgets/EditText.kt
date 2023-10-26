package com.bingyan.bbhust.ui.widgets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.utils.ifElse

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    hint: String,
    value: String,
    isMask: Boolean = false,
    unselectedIcon: Int? = null,
    selectedIcon: Int? = unselectedIcon,
    selectedColor: Color = themeColor,
    singleLine: Boolean = true,
    unselectedColor: Color = Color.LightGray,
    background: Color = unselectedColor.copy(alpha = 0.2f),
    sideContent: @Composable SideContentScope.() -> Unit = {},
    onChange: (String) -> Unit,
) {
    val focused = remember {
        mutableStateOf(false)
    }
    val focusColor =
        animateColorAsState(
            targetValue = if (focused.value) selectedColor else Color.White.copy(
                alpha = 0f
            ), animationSpec = tween(), label = ""
        )
    val focusColor2 =
        animateColorAsState(
            targetValue = if (focused.value) selectedColor else unselectedColor,
            label = ""
        )
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusEvent {
                focused.value = it.isFocused
            },
        value = value,
        visualTransformation = isMask.ifElse(
            PasswordVisualTransformation(), VisualTransformation.None
        ),
        singleLine = singleLine,
        onValueChange = onChange,
        textStyle = TextStyle(fontSize = 16.sp, color = selectedColor),
        cursorBrush = SolidColor(selectedColor)
    ) {
        Row(
            modifier
                .border(
                    2.dp,
                    focusColor.value,
                    RoundedCornerShape(10.dp)
                )
                .padding(2.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .background(background)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (unselectedIcon != null) {
                    Image(
                        painter = painterResource(
                            id = if (focused.value) selectedIcon
                                ?: unselectedIcon else unselectedIcon
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(focusColor2.value)
                    )
                }
                Box(modifier = Modifier, contentAlignment = Alignment.CenterStart) {
                    if (value.isBlank()) Text(text = hint, color = unselectedColor)
                    it()
                }
            }
            SideContentScope(focusColor2.value).sideContent()
        }
    }
}


class SideContentScope(private val color: Color) {
    @Composable
    fun Text(text: String, onClick: () -> Unit) {
        TextButton(
            modifier = Modifier.padding(horizontal = 4.dp), onClick = onClick
        ) {
            Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }

    @Composable
    fun Icon(
        id: Int,
        contentDescription: String,
        onClick: () -> Unit
    ) {
        IconButton(modifier = Modifier.padding(horizontal = 2.dp), onClick = onClick) {
            Icon(
                modifier = Modifier.size(16.dp),
                painter = painterResource(id = id),
                contentDescription = contentDescription,
                tint = color
            )
        }
    }
}


@Preview
@Composable
fun EditTextPreview() {
    val v = remember {
        mutableStateOf("")
    }
    EditText(hint = "邮箱验证码",
        value = v.value,
        unselectedIcon = R.drawable.hashtag,
        background = Color.White,
        onChange = {
            v.value = it
        },
        selectedColor = themeColor,
        sideContent = {
            Text(text = "发送验证码", onClick = {})
        })
}

@Preview
@Composable
fun EditTextPreview2() {
    val v = remember {
        mutableStateOf("")
    }
    EditText(hint = "密码",
        value = v.value,
        unselectedIcon = R.drawable.icon_key_line,
        background = Color.White,
        onChange = {
            v.value = it
        },
        selectedColor = themeColor,
        sideContent = {
            Icon(id = R.drawable.eye_line,
                contentDescription = "",
                onClick = {})
        })
}