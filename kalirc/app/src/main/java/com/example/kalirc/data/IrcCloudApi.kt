package com.example.kalirc.data

import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface IrcCloudApi {
    @FormUrlEncoded
    @POST("chat/auth-formtoken")
    suspend fun login(@Field("email") email: String, @Field("password") password: String): AuthResponse

    @GET
    suspend fun getBacklog(@Url url: String): ResponseBody
}
