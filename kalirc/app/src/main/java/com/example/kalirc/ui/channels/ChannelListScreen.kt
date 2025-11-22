package com.example.kalirc.ui.channels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.kalirc.data.Channel

@Composable
fun ChannelListScreen(channels: List<Channel>, onChannelClick: (Int) -> Unit) {
    LazyColumn {
        items(channels) { channel ->
            Text(text = channel.name, modifier = Modifier.clickable { onChannelClick(channel.bid) })
        }
    }
}
