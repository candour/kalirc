package com.example.kalirc.ui.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.kalirc.R
import com.example.kalirc.data.Message
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@Composable
fun MessageViewScreen(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            when (message.type) {
                "buffer_msg" -> {
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(text = "${message.from}: ")
                        Text(text = message.msg)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)))
                    }
                    val url = extractUrl(message.msg)
                    if (url != null) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            placeholder = painterResource(id = R.drawable.ic_placeholder),
                            error = painterResource(id = R.drawable.ic_placeholder)
                        )
                    }
                }
                "joined_channel", "parted_channel", "quit" -> {
                    Text(text = message.msg, fontStyle = FontStyle.Italic)
                }
            }
        }
    }
}

fun extractUrl(text: String): String? {
    val pattern = Pattern.compile("https?://\\S+\\.(?:jpg|jpeg|png|gif)")
    val matcher = pattern.matcher(text)
    return if (matcher.find()) {
        matcher.group(0)
    } else {
        null
    }
}
