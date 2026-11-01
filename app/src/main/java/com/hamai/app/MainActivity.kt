package com.hamai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            ChatScreen()
        }
    }
}

@Composable
fun ChatScreen() {
    var inputText by rememberSaveable { mutableStateOf("") }
    var botMessage by rememberSaveable {
        mutableStateOf("Salam. Ana HamAI. Kteb lia chi haja.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "HamAI",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = botMessage,
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Message")
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val text = inputText.trim()
                    if (text.isNotEmpty()) {
                        botMessage = createReply(text)
                        inputText = ""
                    }
                }
            ) {
                Text(text = "Send")
            }

            OutlinedButton(
                onClick = {
                    inputText = ""
                    botMessage = "Conversation cleared."
                }
            ) {
                Text(text = "Clear")
            }
        }
    }
}

fun createReply(userText: String): String {
    val text = userText.lowercase()

    return when {
        text.contains("salam") -> "Wa 3alaykom salam."
        text.contains("hello") -> "Hello. How can I help you?"
        text.contains("hi") -> "Hi. How can I help you?"
        text.contains("apk") -> "If GitHub Actions is green, download APK from Artifacts."
        text.contains("merci") -> "Bla jmil."
        text.contains("thanks") -> "You are welcome."
        else -> "I received: " + userText
    }
}
