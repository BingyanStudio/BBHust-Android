package com.bingyan.bbhust.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun <T> keep(v: T): MutableState<T> {
    return remember {
        mutableStateOf(v)
    }
}

@Composable
fun <T> array(): SnapshotStateList<T> {
    return remember {
        mutableStateListOf()
    }
}