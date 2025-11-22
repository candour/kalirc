package com.example.kalirc.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Channel(
    @PrimaryKey @SerializedName("bid") val bid: Int,
    @SerializedName("cid") val cid: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("deferred") val deferred: Boolean,
    @SerializedName("last_seen_eid") val lastSeenEid: Long,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("members") val members: List<String>? = null
)
