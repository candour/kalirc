package com.messark.kalirc.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "buffers")
data class Buffer(
    @PrimaryKey val bid: Int,
    val cid: Int,
    val name: String,
    val type: String, // "channel", "conversation", "console"
    val archived: Boolean
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val eid: Long,
    val bid: Int,
    val cid: Int,
    @ColumnInfo(name = "sender") val from: String?,
    val msg: String?,
    val type: String,
    val timestamp: Long // in microseconds
)

@Dao
interface ChatDao {
    @Query("SELECT * FROM buffers WHERE archived = 0 ORDER BY name ASC")
    fun getActiveBuffers(): Flow<List<Buffer>>

    @Query("SELECT * FROM messages WHERE bid = :bid ORDER BY timestamp ASC")
    fun getMessagesForBuffer(bid: Int): Flow<List<Message>>

    @Query("SELECT * FROM buffers WHERE bid = :bid LIMIT 1")
    suspend fun getBuffer(bid: Int): Buffer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuffers(buffers: List<Buffer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<Message>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("DELETE FROM buffers")
    suspend fun clearBuffers()

    @Query("DELETE FROM messages")
    suspend fun clearMessages()
}

@Database(entities = [Buffer::class, Message::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
