package com.bingyan.bbhust.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.EditText


//关键内容限制长度：@、#话题#、[表情]
//浮动长度，实际查找长度在 25±2 ~ 50±2 之间
const val KEY_LENGTH = 25

@SuppressLint("AppCompatCustomView")
class AppEdit(ctx: Context, attrs: AttributeSet) :
    EditText(ctx, attrs) {

    private lateinit var onSelection: (Int, Int) -> Unit

    constructor(onSelection: (Int, Int) -> Unit, ctx: Context, attrs: AttributeSet) : this(
        ctx,
        attrs
    ) {
        this.onSelection = onSelection
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (::onSelection.isInitialized)
            onSelection(selStart, selEnd)
    }
}