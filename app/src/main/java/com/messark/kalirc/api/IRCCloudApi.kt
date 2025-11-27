package com.messark.kalirc.api

import com.google.gson.JsonObject
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IRCCloudApi {
    @FormUrlEncoded
    @POST("chat/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    // This is a simplification. In reality, one often uses the websocket stream.
    // However, for a simple backlog fetch, we might assume we can get the backlog if we have IDs.
    // To get the list of buffers (channels), we might need to use the stream or a specific OOB endpoint.
    // For this exercise, we will assume a fictional or approximate endpoint if a direct one isn't documented in the brief.
    // Using standard REST conventions for "buffers" or "channels".
    // Actually, commonly one connects to the websocket. But let's try to see if we can fetch initial state via REST.
    // We will use a mockable structure here if the real API is strictly WebSocket for lists.
    // But wait, the prompt implies accessing the API.
    // Let's assume we can get the backlog.
    
    @GET("chat/backlog")
    suspend fun getBacklog(
        @Header("Cookie") sessionCookie: String,
        @Query("cid") connectionId: Int,
        @Query("bid") bufferId: Int,
        @Query("num") num: Int = 50
    ): List<ServerEvent>
}

data class LoginResponse(
    val success: Boolean,
    val session: String?,
    val uid: String?,
    val message: String?
)

data class ServerEvent(
    val cid: Int,
    val bid: Int,
    val eid: Long,
    val type: String,
    val msg: String?,
    val from: String?,
    val from_name: String?, // Some events have real name
    val ts: Long // timestamp in microseconds
)
