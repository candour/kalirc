package com.messark.kalirc.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class IRCCloudWebSocket(
    private val sessionToken: String,
    private val onEvent: (JsonObject) -> Unit
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    fun connect() {
        val request = Request.Builder()
            .url("wss://www.irccloud.com/")
            .header("Origin", "https://www.irccloud.com")
            .header("Cookie", "session=$sessionToken")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("IRCCloudWebSocket", "Connected")
                // We might need to send some init message or just wait for oob_include
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("IRCCloudWebSocket", "Message: $text")
                try {
                    val json = gson.fromJson(text, JsonObject::class.java)
                    onEvent(json)
                } catch (e: Exception) {
                    Log.e("IRCCloudWebSocket", "Error parsing message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("IRCCloudWebSocket", "Closing: $code / $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("IRCCloudWebSocket", "Failure: ${t.message}", t)
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Goodbye")
    }
}
