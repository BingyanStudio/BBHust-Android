package com.bingyan.bbhust.ui.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.bingyan.bbhust.ui.theme.CardShapes
import com.bingyan.bbhust.ui.theme.Gap
import com.bingyan.bbhust.ui.theme.ImageSize
import com.bingyan.bbhust.ui.theme.Transparent
import com.bingyan.bbhust.ui.theme.colors
import com.bingyan.bbhust.ui.theme.themeColor

@Composable
fun LabelTextField(
    modifier: Modifier = Modifier,
    lineLabel: Int = 0,
    fillLabel: Int = 0,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    paddingValues: PaddingValues = PaddingValues(Gap.Big),
    textStyle: TextStyle = LocalTextStyle.current,
    hint: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorColor: Color = colors.primary,
    backgroundColor: Color = colors.card,
    hintColor: Color = colors.textSecondary,
    textColor: Color = colors.textPrimary,
    unfocusedColor: Color = colors.unfocused,
    sideContent: @Composable () -> Unit = {}
) {
    var focused by remember { mutableStateOf(true) }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .background(Transparent)
            .onFocusChanged {
                focused = it.isFocused
            },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(
            color = textColor
        ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(cursorColor)
    ) {
        Row(
            modifier
                /*.border(
                    Border.Small,
                    if (focused) colors.primary else colors.unfocused,
                    RoundedCornerShape(25)
                )*/
                .background(
                    if (focused) Color.Black.copy(alpha = 0.05f) else Color.Transparent,
                    CardShapes.medium
                )
                .background(backgroundColor, CardShapes.medium)
                .padding(paddingValues),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (lineLabel != 0) {
                EasyImage(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(ImageSize.Small),
                    src = if (focused && fillLabel != 0) fillLabel else lineLabel,
                    contentDescription = "",
                    tint = if (focused) themeColor else unfocusedColor,
                )
            }
            Spacer(modifier = Modifier.width(Gap.Mid))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                color = Transparent,
            ) {
                it()
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = textStyle.copy(
                            color = hintColor
                        ),
                        modifier = Modifier.padding(horizontal = Gap.Tiny)
                    )
                }
            }
            sideContent()
        }
    }
}

/**
 * Plain TextField 普通TextEditor(带Hint)
 */
@Composable
fun PlainTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    hint: String,
    hintColor: Color = colors.textSecondary,
    textColor: Color = colors.textPrimary,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorColor: Color = Color.Black
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(textColor),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        onTextLayout = onTextLayout,
        cursorBrush = SolidColor(cursorColor),
        decorationBox = {
            Surface(
                modifier = Modifier,
                color = (Color.Transparent),
            ) {
                it()
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        style = textStyle.copy(
                            color = hintColor
                        ),
                        modifier = Modifier.padding(horizontal = Gap.Tiny)
                    )
                }
            }
        }
    )
}


/**
 * Plain TextField 普通TextEditor(带Hint)
 */
@Composable
fun PlainTextField(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    hint: String,
    hintColor: Color = colors.textSecondary,
    textColor: Color = colors.textPrimary,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorColor: Color = Color.Black
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(textColor),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        interactionSource = interactionSource,
        onTextLayout = onTextLayout,
        cursorBrush = SolidColor(cursorColor),
        decorationBox = {
            Surface(
                modifier = Modifier,
                color = (Color.Transparent),
            ) {
                it()
                if (value.text.isEmpty()) {
                    Text(
                        text = hint,
                        style = textStyle.copy(
                            color = hintColor
                        ),
                        modifier = Modifier.padding(horizontal = Gap.Tiny)
                    )
                }
            }
        }
    )
}

/**
 * Expandable TextField 可全屏TextEditor(带Hint)
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorColor: Color = Color.Black
) {
    val expand by remember {
        mutableStateOf(false)
    }
    AnimatedContent(targetState = expand) { targetState ->
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.height(if (targetState) IntrinsicSize.Max else IntrinsicSize.Min),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            visualTransformation = visualTransformation,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(cursorColor),
            decorationBox = {
                Row(Modifier.padding(Gap.Big)) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(if (targetState) IntrinsicSize.Max else IntrinsicSize.Min),
                        color = (Color.Transparent),
                    ) {
                        it()
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = textStyle.copy(
                                    color = colors.textSecondary

                                ),
                                modifier = Modifier.padding(horizontal = Gap.Tiny)
                            )
                        }
                    }
                }

            }

        )
    }
}