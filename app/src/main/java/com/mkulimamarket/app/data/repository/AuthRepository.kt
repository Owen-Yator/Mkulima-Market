package com.mkulimamarket.app.data.repository

import com.mkulimamarket.app.data.model.Profile
import com.mkulimamarket.app.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest

class AuthRepository {

    private val client = SupabaseClient.client
    private val auth = client.auth

    suspend fun signUp(
        fullName: String,
        email: String,
        phone: String,
        county: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val user = auth.currentUserOrNull()
                ?: throw Exception("User was created but no authenticated session was returned.")

            val profile = Profile(
                id = user.id,
                full_name = fullName,
                email = email,
                phone = phone,
                county = county
            )

            client.postgrest
                .from("profiles")
                .insert(profile)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUserOrNull()

    fun isLoggedIn(): Boolean =
        auth.currentSessionOrNull() != null
}
