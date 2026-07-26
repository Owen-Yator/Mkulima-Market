package com.mkulimamarket.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val full_name: String,
    val email: String,
    val phone: String,
    val county: String,
    val role: String = "user"
)