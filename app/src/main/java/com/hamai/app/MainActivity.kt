package com.hamai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HamAiApp()
        }
    }
}

@Composable
fun HamAiApp() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF0B1020),
            surface = Color(0xFF111827),
            primary = Color(0xFF22C55E),
            onPrimary = Color.Black
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0B1020)
        ) {
            ChatScreen()
        }
    }
}

@Composable
fun ChatScreen() {
    var inputText by rememberSaveable { mutableStateOf("") }

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                text = "Salam 👋 Ana HamAI. Kteb lia chi soual.",
                isUser = false
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
            .padding(16.dp)
            .imePadding()
    ) {
        Header(
            onClearClick = {
                messages.clear()
                messages.add(
                    ChatMessage(
                        text = "Conversation tms7at ✅ Kteb lia chi soual jdid.",
                        isUser = false
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(text = "Kteb hna...")
                },
                maxLines = 4,
                singleLine = false
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val text = inputText.trim()
                    if (text.isNotEmpty()) {
                        messages.add(
                            ChatMessage(
                                text = text,
                                isUser = true
                            )
                        )

                        messages.add(
                            ChatMessage(
                                text = createBotReply(text),
                                isUser = false
                            )
                        )

                        inputText = ""
                    }
                }
            ) {
                Text(text = "Send")
            }
        }
    }
}

@Composable
fun Header(
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "HamAI",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Simple AI Chat App",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp
            )
        }

        OutlinedButton(
            onClick = onClearClick
        ) {
            Text(text = "Clear")
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.82f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser) {
                    Color(0xFF22C55E)
                } else {
                    Color(0xFF111827)
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Text(
                    text = if (message.isUser) "You" else "HamAI",
                    color = if (message.isUser) Color.Black else Color(0xFF22C55E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message.text,
                    color = if (message.isUser) Color.Black else Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

fun createBotReply(userText: String): String {
    val text = userText.lowercase()

    return when {
        "salam" in text || "hello" in text || "hi" in text -> {
            "Salam 👋 Kifach nقدر nعاونك?"
        }

        "apk" in text -> {
            "باش تصاوب APK، خاص GitHub Actions يكمل باللون الأخضر