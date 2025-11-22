package com.example.kalirc.data

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.kalirc.data.database.AppDatabase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import java.util.concurrent.TimeUnit

class IrcCloudRepository(application: Application, private val coroutineScope: CoroutineScope) {

    private val channelDao = AppDatabase.getDatabase(application).channelDao()
    private val messageDao = AppDatabase.getDatabase(application).messageDao()

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val retrofit = retrofit2.Retrofit.Builder()
        .baseUrl("https://www.irccloud.com/")
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(IrcCloudApi::class.java)

    private var webSocket: WebSocket? = null

    fun connect(session: String) {
        val request = Request.Builder()
            .url("wss://api.irccloud.com/")
            .addHeader("Cookie", "session=$session")
            .addHeader("Origin", "https://api.irccloud.com")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val gson = Gson()
                val message = gson.fromJson(text, Map::class.java)
                when (message["type"]) {
                    "oob_include" -> {
                        val url = message["url"] as String
                        fetchBacklog(url)
                    }
                    "makebuffer" -> {
                        val channel = gson.fromJson(text, Channel::class.java)
                        insertChannel(channel)
                    }
                    "buffer_msg" -> {
                        val msg = gson.fromJson(text, Message::class.java)
                        insertMessage(msg)
                    }
                }
            }
        })
    }

    private fun fetchBacklog(url: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val response = api.getBacklog(url)
            val backlog = response.string()
            val gson = Gson()
            val channels = mutableListOf<Channel>()
            backlog.lines().forEach {
                val message = gson.fromJson(it, Map::class.java)
                if (message["type"] == "makebuffer") {
                    channels.add(gson.fromJson(it, Channel::class.java))
                }
            }
            channelDao.insertChannels(channels)
        }
    }

    private fun insertChannel(channel: Channel) {
        coroutineScope.launch(Dispatchers.IO) {
            channelDao.insertChannels(listOf(channel))
        }
    }

    private fun insertMessage(message: Message) {
        coroutineScope.launch(Dispatchers.IO) {
            messageDao.insertMessages(listOf(message))
        }
    }
}
