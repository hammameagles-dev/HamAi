package com.hamai.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class Msg(val role: String, val text: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HamAI() }
    }
}

@Composable
fun HamAI() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { ctx.getSharedPreferences("hamai", Context.MODE_PRIVATE) }

    val green = Color(0xFF00C853)
    val dark = Color(0xFF050805)
    val card = Color(0xFF101810)

    var apiKey by remember { mutableStateOf(prefs.getString("key", "") ?: "") }
    var showKey by remember { mutableStateOf(apiKey.isBlank()) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val models = listOf(
        "nousresearch/hermes-3-llama-3.1-70b",
        "cognitivecomputations/dolphin-mixtral-8x22b",
        "mistralai/mixtral-8x7b-instruct",
        "qwen/qwen-2.5-72b-instruct",
        "deepseek/deepseek-chat",
        "meta-llama/llama-3.1-70b-instruct",
        "meta-llama/llama-3.1-8b-instruct",
        "mistralai/mistral-7b-instruct"
    )

    var selectedModel by remember {
        mutableStateOf(prefs.getString("model", models.first()) ?: models.first())
    }

    var menu by remember { mutableStateOf(false) }
    val messages = remember { mutableStateListOf<Msg>() }
    val scope = rememberCoroutineScope()

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = green,
            background = dark,
            surface = card,
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(dark)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("HamAI", color = green, style = MaterialTheme.typography.headlineMedium)
                    Text("Direct private AI chat", color = Color(0xFFB8C8B8))
                }

                IconButton(onClick = { showKey = true }) {
                    Icon(Icons.Default.Key, null, tint = green)
                }

                IconButton(onClick = {
                    messages.clear()
                    error = ""
                }) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFFF7777))
                }
            }

            Spacer(Modifier.height(10.dp))

            Box {
                Button(
                    onClick = { menu = true },
                    colors = ButtonDefaults.buttonColors(containerColor = card)
                ) {
                    Text(selectedModel, color = Color.White)
                }

                DropdownMenu(
                    expanded = menu,
                    onDismissRequest = { menu = false },
                    modifier = Modifier.background(card)
                ) {
                    models.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m, color = Color.White) },
                            onClick = {
                                selectedModel = m
                                prefs.edit().putString("model", m).apply()
                                menu = false
                            }
                        )
                    }
                }
            }

            if (error.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = Color(0xFFFF7777))
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("كتب شي رسالة باش تبدا", color = Color(0xFFB8C8B8))
                        }
                    }
                }

                items(messages) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            color = if (isUser) Color(0xFF0B3D1B) else Color(0xFF182018),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .widthIn(max = 320.dp)
                        ) {
                            Text(
                                text = msg.text,
                                color = Color.White,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                if (loading) {
                    item {
                        Text("HamAI kayfker...", color = green, modifier = Modifier.padding(8.dp))
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Message...") },
                    keyboardOptions = Keyboard