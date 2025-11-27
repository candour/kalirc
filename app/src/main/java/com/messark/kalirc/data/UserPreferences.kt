package com.messark.kalirc.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class UserPreferences(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSession(session: String) {
        sharedPreferences.edit().putString("session", session).apply()
    }

    fun getSession(): String? {
        return sharedPreferences.getString("session", null)
    }

    fun clearSession() {
        sharedPreferences.edit().remove("session").apply()
    }
    
    fun saveCredentials(email: String, pass: String) {
        sharedPreferences.edit()
            .putString("email", email)
            .putString("password", pass)
            .apply()
    }

    fun getCredentials(): Pair<String?, String?> {
        return Pair(
            sharedPreferences.getString("email", null),
            sharedPreferences.getString("password", null)
        )
    }
}
