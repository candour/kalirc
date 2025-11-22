package com.example.kalirc.security

import android.content.Context
import android.content.SharedPreferences

class CredentialsManager(context: Context) {

    private val keystoreWrapper = KeystoreWrapper()
    private val prefs: SharedPreferences = context.getSharedPreferences("kalirc_prefs", Context.MODE_PRIVATE)

    fun saveCredentials(email: String, password: String) {
        val (emailIv, encryptedEmail) = keystoreWrapper.encrypt(email)
        val (passwordIv, encryptedPassword) = keystoreWrapper.encrypt(password)

        prefs.edit()
            .putString("email", android.util.Base64.encodeToString(encryptedEmail, android.util.Base64.DEFAULT))
            .putString("email_iv", android.util.Base64.encodeToString(emailIv, android.util.Base64.DEFAULT))
            .putString("password", android.util.Base64.encodeToString(encryptedPassword, android.util.Base64.DEFAULT))
            .putString("password_iv", android.util.Base64.encodeToString(passwordIv, android.util.Base64.DEFAULT))
            .apply()
    }

    fun getCredentials(): Pair<String, String>? {
        val email = prefs.getString("email", null)
        val emailIv = prefs.getString("email_iv", null)
        val password = prefs.getString("password", null)
        val passwordIv = prefs.getString("password_iv", null)

        if (email == null || emailIv == null || password == null || passwordIv == null) {
            return null
        }

        val decryptedEmail = keystoreWrapper.decrypt(
            android.util.Base64.decode(emailIv, android.util.Base64.DEFAULT),
            android.util.Base64.decode(email, android.util.Base64.DEFAULT)
        )
        val decryptedPassword = keystoreWrapper.decrypt(
            android.util.Base64.decode(passwordIv, android.util.Base64.DEFAULT),
            android.util.Base64.decode(password, android.util.Base64.DEFAULT)
        )

        return Pair(decryptedEmail, decryptedPassword)
    }

    fun clearCredentials() {
        prefs.edit().clear().apply()
    }
}
