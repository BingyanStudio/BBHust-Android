package com.bingyan.bbhust.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with

object AppTypography {
    val title: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.W700
    )
    val subtitle: TextStyle = TextStyle(
        fontSize = 16.sp
    )
    val body: TextStyle = TextStyle(
        fontSize = 16.sp
    )
    val card_title: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W700
    )
    val card_title_small: TextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.W700
    )
    val assets_big: TextStyle = TextStyle(
        fontSize = 22.sp
    )
    val assets_normal: TextStyle = TextStyle(
        fontSize = 16.sp
    )
    val item_title: TextStyle = TextStyle(
        fontSize = 12.sp
    )
    val description: TextStyle = TextStyle(
        fontSize = 10.sp
    )
    val platformDescription: TextStyle = TextStyle(
        fontSize = 12.sp,
    )
    val description_right = TextStyle(
        fontSize = 10.sp,
        textAlign = TextAlign.Justify
    )
    val bandage: TextStyle = TextStyle(
        fontSize = 8.sp
    )
    val price: TextStyle = TextStyle(
        fontSize = 14.sp
    )
    val item_title_big: TextStyle = TextStyle(
        fontSize = 16.sp
    )
    val progress_content: TextStyle = TextStyle(
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.W700
    )

}

object MarkTypography {
    val h1: TextStyle = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.W700
    )
    val h2: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.W700
    )
    val h3: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.W700
    )
    val h4: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.W400
    )
    val h5: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.W300
    )
    val h6: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.W100
    )
    val body1: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
    val body2: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Thin
    )
}


//fun markdownTypography() = DefaultMarkdownTypography(
//    h1 = MarkTypography.h1,
//    h2 = MarkTypography.h2,
//    h3 = MarkTypography.h3,
//    h4 = MarkTypography.h4,
//    h5 = MarkTypography.h5,
//    h6 = MarkTypography.h6,
//    body1 = MarkTypography.body1,
//    body2 = MarkTypography.body2,
//)
