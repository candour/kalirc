package com.example.kalirc.ui.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kalirc.data.Message
import com.example.kalirc.data.database.AppDatabase
import kotlinx.coroutines.flow.Flow

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val messageDao = AppDatabase.getDatabase(application).messageDao()

    fun getMessages(bid: Int): Flow<List<Message>> {
        return messageDao.getMessages(bid)
    }
}
