package com.messark.kalirc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.messark.kalirc.data.ChatRepository
import com.messark.kalirc.ui.BufferListScreen
import com.messark.kalirc.ui.ChatScreen
import com.messark.kalirc.ui.LoginScreen
import com.messark.kalirc.ui.theme.KalircTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val application = application as KalircApplication
        val repository = ChatRepository(application.database.chatDao(), application.userPreferences)

        setContent {
            KalircTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(repository)
                }
            }
        }
        
        // Start connection if logged in
        if (repository.isLoggedIn()) {
            repository.connect()
        }
    }
}

@Composable
fun AppNavigation(repository: ChatRepository) {
    val navController = rememberNavController()
    val startDestination = if (repository.isLoggedIn()) "buffers" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                repository = repository,
                onLoginSuccess = {
                    repository.connect()
                    navController.navigate("buffers") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("buffers") {
            BufferListScreen(
                repository = repository,
                onBufferSelected = { bid ->
                    navController.navigate("chat/$bid")
                }
            )
        }
        composable(
            "chat/{bid}",
            arguments = listOf(navArgument("bid") { type = NavType.IntType })
        ) { backStackEntry ->
            val bid = backStackEntry.arguments?.getInt("bid") ?: return@composable
            ChatScreen(bid = bid, repository = repository)
        }
    }
}
