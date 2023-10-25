package com.bingyan.bbhust.base

/**
 * MVI 架构 State 通用状态集合
 */
/// 通用三态：闲置、加载、错误
sealed class TriState {
    object Idle : TriState()
    object Loading : TriState()
    data class Error(val msg: String) : TriState()
}

/// 通用四态：闲置、加载（刷新）、加载更多、错误
sealed class FourState {
    object Idle : FourState()
    object Loading : FourState()
    object OnMore : FourState()
    data class Error(val msg: String) : FourState()
}

/// 通用请求四态：闲置、请求中、成功、错误
sealed class MutationState {
    object Idle : MutationState()
    object Requesting : MutationState()
    object Success : MutationState()
    data class Error(val msg: String) : MutationState()
}