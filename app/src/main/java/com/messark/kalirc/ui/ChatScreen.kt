package com.messark.kalirc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.messark.kalirc.data.ChatRepository
import com.messark.kalirc.data.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    bid: Int,
    repository: ChatRepository
) {
    val messages by repository.getMessages(bid).collectAsState(initial = emptyList())

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = true // Usually chat is bottom-up, but here let's keep it simple top-down or use reverse if we auto-scroll
        ) {
            // Messages are sorted by timestamp ASC.
            // If we want newest at bottom (standard chat), we just iterate normally but might need auto-scroll.
            // For simplicity, we will just display them.
            items(messages) { message ->
                MessageItem(message = message)
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Username
            Text(
                text = "${message.from ?: "System"}: ",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 4.dp)
            )

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
            ) {
                Column {
                    if (!message.msg.isNullOrEmpty()) {
                         // Check if message is an image URL (very basic check)
                         if (isImageUrl(message.msg)) {
                             AsyncImage(
                                 model = message.msg,
                                 contentDescription = "Shared image",
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .clip(RoundedCornerShape(8.dp)),
                                 contentScale = ContentScale.Crop
                             )
                         } else {
                             Text(
                                 text = message.msg,
                                 style = MaterialTheme.typography.bodyMedium
                             )
                         }
                    }
                }
            }

            // Time
            Text(
                text = formatTime(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.Bottom)
            )
        }
    }
}

fun isImageUrl(url: String): Boolean {
    return url.startsWith("http") && (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".gif") || url.endsWith(".jpeg"))
}

fun formatTime(timestampMicros: Long): String {
    val date = Date(timestampMicros / 1000)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    return format.format(date)
}
