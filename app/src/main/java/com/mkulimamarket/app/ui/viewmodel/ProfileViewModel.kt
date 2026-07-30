package com.mkulimamarket.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mkulimamarket.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UserProfileState(
    val name: String = "",
    val email: String = "",
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _userState = MutableStateFlow(UserProfileState())
    val userState = _userState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            // Retrieve current user metadata / email from auth session
            val user = authRepository.getCurrentUser()
            val fullName = user?.userMetadata?.get("full_name")?.toString()
                ?: user?.email?.substringBefore("@")
                ?: "Mkulima User"
            val email = user?.email ?: "No email available"

            _userState.value = UserProfileState(
                name = fullName,
                email = email,
                isLoading = false
            )
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onComplete()
        }
    }
}
