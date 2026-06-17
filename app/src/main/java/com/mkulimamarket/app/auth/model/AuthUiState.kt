package com.mkulimamarket.app.auth.model

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val phone: String = "",
    val county: String = "",
    val role: String = "Farmer",
    val isLoading: Boolean = false,
    val error: String? = null
)