package com.bingyan.bbhust.base

import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bingyan.bbhust.App
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KProperty

/**
 * MVI 架构 ViewModel 基类
 * S: State
 * A: Action
 * 通过 Action 更新 State
 *
 */
abstract class BaseViewModel<S, A>(s: S) : ViewModel() {
    var state: S by Keep(s, ::onChange)
    private var baseState: BaseState by Keep(BaseState()) { old, new ->
        if (new.toast.isNotBlank() && new.toast != old.toast) {
            Toast.makeText(App.CONTEXT, new.toast, Toast.LENGTH_SHORT).show()
            viewModelScope.launch {
                delay(2000) // 可过滤掉当前 toast 显示期间相同内容的新 toast
                baseState = baseState.copy(toast = "")
            }
        }
    }

    /// 重载 send 方法，使其可以直接传入 Action
    fun A.send() {
        state = reduce(this)
    }

    /// 执行匿名函数所返回的 Action
    infix fun act(action: () -> A) {
        state = reduce(action())
    }

    /// 重载 act 方法，使其可以直接传入 Action
    infix fun act(action: A) {
        state = reduce(action)
    }

    /**
     * 立即更新值,并在之后执行 action
     */
    inline fun S.then(action: () -> Unit): S {
        state = this
        action()
        return state
    }

    /**
     * 立即更新值
     */
    fun S.update() {
        state = this
    }

    /**
     * 展示 Toast
     */
    fun toast(msg: String) {
        baseState = baseState.copy(toast = msg)
    }

    /**
     * 处理 Action,返回新的 State
     *
     * 子类继承实现
     */
    protected abstract fun reduce(action: A): S

    /**
     * State 变化时的回调
     *
     * 需要对 State 进行监听时重写该方法
     */
    protected open fun onChange(old: S, new: S) {}
}

/**
 * 用于 ViewModel 中的属性委托, 可以监听属性值的变化
 */
class Keep<T>(v: T, private val onChange: (T, T) -> Unit = { _, _ -> }) {
    private val v: MutableState<T> = mutableStateOf(v)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return v.value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        // 可以尝试做 Time Travel
        if (v.value == value) return
        onChange(v.value, value)
        v.value = value
    }
}