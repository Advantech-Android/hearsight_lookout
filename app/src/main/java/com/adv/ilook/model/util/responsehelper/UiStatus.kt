package com.adv.ilook.model.util.responsehelper

sealed class UiStatus {
    data object Idle : UiStatus()
    data object Loading : UiStatus()
    class Success(val data: String) : UiStatus()
    class Error(val data: String) : UiStatus()
}