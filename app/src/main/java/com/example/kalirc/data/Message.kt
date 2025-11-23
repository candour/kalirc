package com.example.kalirc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Message(
    @PrimaryKey @SerializedName("eid") val eid: Long,
    @SerializedName("cid") val cid: Int,
    @SerializedName("bid") val bid: Int,
    @SerializedName("from") val from: String,
    @SerializedName("msg") val msg: String,
    @SerializedName("type") val type: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("self") val self: Boolean
)
