package com.example.kalirc.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalirc.data.AuthResponse
import com.example.kalirc.data.IrcCloudApi
import com.example.kalirc.security.CredentialsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val credentialsManager = CredentialsManager(application)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.irccloud.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(IrcCloudApi::class.java)

    init {
        val credentials = credentialsManager.getCredentials()
        if (credentials != null) {
            login(credentials.first, credentials.second, true)
        }
    }

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = api.login(email, password)
                if (response.success) {
                    if (rememberMe) {
                        credentialsManager.saveCredentials(email, password)
                    }
                    _loginState.value = LoginState.Success(response)
                } else {
                    _loginState.value = LoginState.Error(response.message)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val authResponse: AuthResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}
