package com.messark.kalirc

import android.app.Application
import androidx.room.Room
import com.messark.kalirc.data.AppDatabase
import com.messark.kalirc.data.UserPreferences

class KalircApplication : Application() {
    lateinit var database: AppDatabase
    lateinit var userPreferences: UserPreferences

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "kalirc-database"
        ).build()
        userPreferences = UserPreferences(this)
    }
}
