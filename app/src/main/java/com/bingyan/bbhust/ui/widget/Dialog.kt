package com.bingyan.bbhust.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.bingyan.bbhust.R
import com.bingyan.bbhust.ui.theme.themeColor
import com.bingyan.bbhust.utils.string

class AppDialog(title: String? = null, message: String? = null) {
    private var composable: MutableList<@Composable () -> Unit> = mutableListOf()
    private var negativeButton: @Composable RowScope.(() -> Unit) -> Unit = {}
    private var positiveButton: @Composable RowScope.(() -> Unit) -> Unit = {}

    init {
        if (title != null) composable.add { Title(title) }
        if (message != null) composable.add { Message(message) }
    }

    fun withCheckBox(checked: Boolean, prompt: String, onChanged: (Boolean) -> Unit): AppDialog {
        composable.add { Check(checked, prompt, onChanged) }
        return this
    }

    fun withHeadImage(image: String): AppDialog {
        composable.add(0) { HeadImage(image) }
        return this
    }

    fun withTitle(title: String): AppDialog {
        composable.add { Title(title) }
        return this
    }

    fun withMessage(msg: String): AppDialog {
        composable.add { Message(msg) }
        return this
    }

    fun withRatioList(list: List<String>, selected: Int, onSelected: (Int) -> Unit): AppDialog {
        composable.add { RatioList(list, selected, onSelected) }
        return this
    }

    fun withMultipleSelectList(list: List<Pair<String, () -> Unit>>): AppDialog {
        composable.add { MultipleSelectList(list = list) }
        return this
    }

    fun withView(
        content: @Composable () -> Unit
    ): AppDialog {
        composable.add {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
            ) {
                content()
            }
        }
        return this
    }

    fun positive(text: String = string(R.string.yes), onClick: () -> Boolean): AppDialog {
        positiveButton = {
            Button(text, true) {
                if (onClick()) it() // Dismiss
            }
        }
        return this
    }

    fun negative(text: String = string(R.string.cancel), onClick: () -> Boolean): AppDialog {
        negativeButton = {
            Button(text, false) {
                if (onClick()) it() // Dismiss
            }
        }
        return this
    }


    @Composable
    fun Build(
        show: Boolean,
        properties: DialogProperties = DialogProperties(),
        onDismissRequest: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Box {
            if (show) {
                Dialog(onDismissRequest = {
                    onDismissRequest()
                }, properties = properties) {
                    Column(
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        composable.forEach { it() }
                        if (negativeButton != {} || positiveButton != {}) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    16.dp,
                                    Alignment.CenterHorizontally
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                negativeButton {
                                    onDismissRequest()
                                }
                                positiveButton {
                                    onDismissRequest()
                                }
                            }
                        }
                    }
                }
            }
            content()
        }
    }

    companion object {
        @Composable
        private fun HeadImage(image: String) {
            AsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                contentScale = ContentScale.Crop
            )
        }

        @Composable
        private fun Title(title: String) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            )
        }


        @Composable
        private fun Message(message: String) {
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        @Composable
        private fun Check(checked: Boolean, prompt: String, onChanged: (Boolean) -> Unit) {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                AppCheckBox(checked = checked, onChanged = onChanged)
                Text(
                    modifier = Modifier
                        .offset(x = (-8).dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onChanged(!checked) },
                    text = prompt,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
        }

        @Composable
        private fun RatioList(list: List<String>, selected: Int, onSelected: (Int) -> Unit) {

        }

        @Composable
        private fun MultipleSelectList(list: List<Pair<String, () -> Unit>>) {

        }

        @Composable
        private fun RowScope.Button(text: String, positive: Boolean, onClick: () -> Unit) {
            TextButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = if (positive) buttonColor() else buttonColorSecondary(),
                onClick = onClick
            ) {
                Box(
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Composable
fun buttonColor() = ButtonDefaults.textButtonColors(
    containerColor = themeColor,
    contentColor = Color.White,
)


@Composable
fun buttonColorSecondary() = ButtonDefaults.textButtonColors(
    contentColor = Color.DarkGray,
    containerColor = Color.LightGray.copy(alpha = 0.2f),
)


@Preview
@Composable
fun DialogPreview() {
    val checked = remember { mutableStateOf(true) }
    val show = remember { mutableStateOf(true) }
    AppDialog(
        "Hiden apps", "you can recover the apps you hid from app page at any time here."
    )
//        .withHeadImage("https://images.unsplash.com/photo-1679423137829-3a3de1d5cb8a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=663&q=80")
        .withView {
            EditText(hint = "Remote Server URL", value = "") {

            }
        }
        .withCheckBox(checked.value, "Do not show it anymore") {
            checked.value = !checked.value
        }.positive("Confirm") {
            println("confirm")
            true
        }.negative("Cancel") {
            println("confirm")
            true
        }.Build(show.value, DialogProperties(), onDismissRequest = { show.value = false }) { }
}