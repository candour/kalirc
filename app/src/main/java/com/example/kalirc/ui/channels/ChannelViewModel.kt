package com.example.kalirc.ui.channels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalirc.data.Channel
import com.example.kalirc.data.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class ChannelViewModel(application: Application) : AndroidViewModel(application) {

    private val channelDao = AppDatabase.getDatabase(application).channelDao()
    val channels: Flow<List<Channel>> = channelDao.getChannels()
}
