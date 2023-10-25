package com.bingyan.bbhust.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)
val CardShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(15.dp),
    large = RoundedCornerShape(15.dp)
)
val RoundedShapes = Shapes(
    small = RoundedCornerShape(50),
    medium = RoundedCornerShape(50),
    large = RoundedCornerShape(50)
)
val CardShapesTopHalf = Shapes(
    small = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    medium = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    large = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
)