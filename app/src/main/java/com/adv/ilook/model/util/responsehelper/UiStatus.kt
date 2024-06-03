package com.adv.ilook.model.util.responsehelper

import org.checkerframework.checker.guieffect.qual.UI

sealed class UiStatus {
    data object Idle : UiStatus()
    data object Error : UiStatus()
    data object Loading : UiStatus()
    data object Success : UiStatus()
    data class LoginFormState(
        val usernameError: Int? = null,
        val phoneError: Int? = null,
        val message: Int? = null,
        val isDataValid: Boolean = false
    ) : UiStatus()

    data class OtpFormState(
        val usernameError: Int? = null,
        val phoneError: Int? = null,
        val message: Int? = null,
        val isDataValid: Boolean = false
    ) : UiStatus()

    override fun toString(): String {
        return when (this) {
            is Nothing -> ""
            is Idle -> "Idle"
            is Loading -> "Loading"
            is Success -> "Success"
            is LoginFormState -> "LoginFormState"
            is OtpFormState -> "OtpFormState"
            is Error -> "Error"


        }
    }
}
