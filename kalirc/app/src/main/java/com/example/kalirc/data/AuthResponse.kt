package com.example.kalirc.data

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("session") val session: String,
    @SerializedName("message") val message: String
)
