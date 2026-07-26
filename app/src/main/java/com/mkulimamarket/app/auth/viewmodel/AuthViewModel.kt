package com.mkulimamarket.app.auth.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.auth.model.AuthUiState
import com.mkulimamarket.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    val uiState = mutableStateOf(AuthUiState())

    // ── Supabase State ────────────────────────────────────────────────────────
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    // ── Field Updates ─────────────────────────────────────────────────────────
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

    // ── Validations ───────────────────────────────────────────────────────────
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

    // ── Supabase Auth Actions ──────────────────────────────────────────────────
    fun signUp(onSuccess: () -> Unit) {
        if (!validateSignup()) {
            _message.value = "Please fill in all required fields."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _message.value = ""

            val result = repository.signUp(
                fullName = uiState.value.fullName,
                email = uiState.value.email,
                phone = uiState.value.phone,
                county = uiState.value.county,
                password = uiState.value.password
            )

            _loading.value = false

            result.onSuccess {
                onSuccess()
            }
            result.onFailure { exception ->
                _message.value = exception.message ?: "Signup failed. Please try again."
            }
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (!validateLogin()) {
            _message.value = "Please enter both email and password."
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _message.value = ""

            val result = repository.login(
                email = uiState.value.email,
                password = uiState.value.password
            )

            _loading.value = false

            result.onSuccess {
                onSuccess()
            }
            result.onFailure { exception ->
                _message.value = exception.message ?: "Login failed. Please check your credentials."
            }
        }
    }
}