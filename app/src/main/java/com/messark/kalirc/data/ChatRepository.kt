package com.messark.kalirc.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.messark.kalirc.api.IRCCloudApi
import com.messark.kalirc.api.IRCCloudWebSocket
import com.messark.kalirc.api.LoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChatRepository(
    private val chatDao: ChatDao,
    private val userPreferences: UserPreferences
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.irccloud.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(IRCCloudApi::class.java)
    private var webSocket: IRCCloudWebSocket? = null
    private val gson = Gson()

    val buffers = chatDao.getActiveBuffers()

    fun getMessages(bid: Int) = chatDao.getMessagesForBuffer(bid)

    suspend fun login(email: String, pass: String): Boolean {
        return try {
            val response = api.login(email, pass)
            if (response.success && response.session != null) {
                userPreferences.saveSession(response.session)
                userPreferences.saveCredentials(email, pass)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Login failed", e)
            false
        }
    }

    fun connect() {
        val session = userPreferences.getSession() ?: return
        if (webSocket != null) return // Already connected or connecting

        webSocket = IRCCloudWebSocket(session) { json ->
            handleEvent(json)
        }
        webSocket?.connect()
    }

    private fun handleEvent(json: JsonObject) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (!json.has("type")) return@launch
                val type = json.get("type").asString

                when (type) {
                    "makebuffer" -> {
                        val bid = json.get("bid").asInt
                        val cid = json.get("cid").asInt
                        val name = json.get("name").asString
                        val bufferType = json.get("buffer_type").asString
                        val archived = if (json.has("archived")) json.get("archived").asBoolean else false
                        
                        chatDao.insertBuffers(listOf(Buffer(bid, cid, name, bufferType, archived)))
                    }
                    "buffer_msg" -> {
                        parseAndInsertMessage(json)
                    }
                    "oob_include" -> {
                         // In a real app, we would fetch the OOB url. 
                         // For this simplified version, we rely on live events or assume we can ignore it 
                         // if we don't implement the full sync protocol.
                    }
                    else -> {
                        // Check if it's a backlog or list of events (some custom implementation might do this)
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatRepository", "Error handling event", e)
            }
        }
    }

    private suspend fun parseAndInsertMessage(json: JsonObject) {
        val bid = json.get("bid").asInt
        val cid = json.get("cid").asInt
        val eid = json.get("eid").asLong
        val msg = if (json.has("msg")) json.get("msg").asString else null
        val from = if (json.has("from")) json.get("from").asString else null
        val ts = eid // EID is microseconds timestamp
        val type = json.get("type").asString

        chatDao.insertMessage(Message(eid, bid, cid, from, msg, type, ts))
    }

    fun disconnect() {
        webSocket?.disconnect()
        webSocket = null
    }
    
    fun isLoggedIn(): Boolean {
        return userPreferences.getSession() != null
    }
}
