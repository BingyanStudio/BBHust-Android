package com.bingyan.bbhust.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bingyan.bbhust.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
)
val body = TextStyle()
val h1 = body.copy(
    fontSize = 28.sp,
    fontWeight = FontWeight.W900
)
val h2 = body.copy(
    fontSize = 26.sp,
    fontWeight = FontWeight.W900
)
val h3 = body.copy(
    fontSize = 24.sp,
    fontWeight = FontWeight.W900
)
val h4 = body.copy(
    fontSize = 22.sp,
    fontWeight = FontWeight.W900
)
val h5 = body.copy(
    fontSize = 20.sp,
    fontWeight = FontWeight.W900
)
val h6 = body.copy(
    fontSize = 18.sp,
    fontWeight = FontWeight.W900
)

fun comfortaa(): FontFamily {
    return FontFamily(
        Font(R.font.comfortaa),
    )
}