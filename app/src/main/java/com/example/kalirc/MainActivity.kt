package com.example.kalirc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import com.example.kalirc.ui.channels.ChannelListScreen
import com.example.kalirc.ui.channels.ChannelViewModel
import com.example.kalirc.ui.login.LoginScreen
import com.example.kalirc.ui.login.LoginState
import com.example.kalirc.ui.login.LoginViewModel
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.example.kalirc.data.IrcCloudRepository
import com.example.kalirc.ui.messages.MessageViewModel
import com.example.kalirc.ui.messages.MessageViewScreen
import androidx.lifecycle.lifecycleScope
import com.example.kalirc.ui.theme.KalircTheme
import com.example.kalirc.util.NetworkConnectivityObserver

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private val channelViewModel: ChannelViewModel by viewModels()
    private val messageViewModel: MessageViewModel by viewModels()
    private lateinit var connectivityObserver: NetworkConnectivityObserver
    private lateinit var repository: IrcCloudRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        repository = IrcCloudRepository(application, lifecycleScope)

        val imageLoader = ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(100 * 1024 * 1024)
                    .build()
            }
            .respectCacheHeaders(false)
            .build()

        setContent {
            KalircTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val loginState by loginViewModel.loginState.collectAsState()
                    val channels by channelViewModel.channels.collectAsState(initial = emptyList())
                    var selectedChannel by remember { mutableStateOf<Int?>(null) }
                    val messages by if (selectedChannel != null) {
                        messageViewModel.getMessages(selectedChannel!!).collectAsState(initial = emptyList())
                    } else {
                        remember { mutableStateOf(emptyList()) }
                    }
                    val isOnline by connectivityObserver.status.collectAsState(initial = true)

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val state = loginState) {
                            is LoginState.Success -> {
                                LaunchedEffect(isOnline, state.authResponse.session) {
                                    if (isOnline) {
                                        repository.connect(state.authResponse.session)
                                    }
                                }
                                if (selectedChannel == null) {
                                    ChannelListScreen(channels = channels) {
                                        selectedChannel = it
                                    }
                                } else {
                                    MessageViewScreen(messages = messages)
                                }
                            }
                            is LoginState.Error -> {
                                LoginScreen(errorMessage = state.message) { username, password, rememberMe ->
                                    loginViewModel.login(username, password, rememberMe)
                                }
                            }
                            else -> {
                                LoginScreen(errorMessage = null) { username, password, rememberMe ->
                                    loginViewModel.login(username, password, rememberMe)
                                }
                            }
                        }
                        if (!isOnline) {
                            Text(
                                text = "Disconnected",
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
