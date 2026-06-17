package com.mkulimamarket.app.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mkulimamarket.app.auth.model.AuthUiState

class AuthViewModel : ViewModel() {

    val uiState = mutableStateOf(AuthUiState())

    fun updateEmail(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun updatePassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun updateFullName(name: String) {
        uiState.value = uiState.value.copy(fullName = name)
    }

    fun updatePhone(phone: String) {
        uiState.value = uiState.value.copy(phone = phone)
    }

    fun updateCounty(county: String) {
        uiState.value = uiState.value.copy(county = county)
    }

    fun updateRole(role: String) {
        uiState.value = uiState.value.copy(role = role)
    }

    fun validateLogin(): Boolean {
        return uiState.value.email.isNotBlank() &&
                uiState.value.password.isNotBlank()
    }

    fun validateSignup(): Boolean {
        return uiState.value.fullName.isNotBlank() &&
                uiState.value.email.isNotBlank() &&
                uiState.value.phone.isNotBlank() &&
                uiState.value.password.isNotBlank()
    }
}