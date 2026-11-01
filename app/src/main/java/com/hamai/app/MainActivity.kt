package com.hamai.app

import android.content.Context
import android.os.Bundle
import android.text.InputType
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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

data class ChatMessage(
    val role: String,
    val content: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HamAIApp()
        }
    }
}

@Composable
fun HamAIApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("hamai_prefs", Context.MODE_PRIVATE)
    }

    val green = Color(0xFF00C853)
    val dark = Color(0xFF050805)
    val card = Color(0xFF101810)
    val userBubble = Color(0xFF0B3D1B)
    val aiBubble = Color(0xFF182018)

    var apiKey by remember { mutableStateOf(prefs.getString("api_key", "") ?: "") }
    var showKeyDialog by remember { mutableStateOf(apiKey.isBlank()) }
    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val models = remember {
        listOf(
            "nousresearch/hermes-3-llama-3.1-70b",
            "cognitivecomputations/dolphin-mixtral-8x22b",
            "mistralai/mixtral-8x7b-instruct",
            "qwen/qwen-2.5-72b-instruct",
            "deepseek/deepseek-chat",
            "meta-llama/llama-3.1-70b-instruct",
            "meta-llama/llama-3.1-8b-instruct",
            "mistralai/mistral-7b-instruct"
        )
    }

    var selectedModel by remember {
        mutableStateOf(prefs.getString("model", models.first()) ?: models.first())
    }

    val messages = remember {
        mutableStateListOf<ChatMessage>().apply {
            addAll(loadHistory(prefs))
        }
    }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var menuOpen by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = green,
            background = dark,
            surface = card,
            onPrimary = Color.Black,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "HamAI",
                        color = green,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Direct private AI chat",
                        color = Color(0xFFB8C8B8),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(onClick = { showKeyDialog = true }) {
                    Icon(Icons.Default.Key, contentDescription = "API Key", tint = green)
                }

                IconButton(onClick = {
                    messages.clear()
                    saveHistory(prefs, messages)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color(0xFFFF7070))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box {
                Button(
                    onClick = { menuOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = card)
                ) {
                    Text(
                        text = selectedModel,
                        color = Color.White
                    )
                }

                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier.background(card)
                ) {
                    models.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model, color = Color.White) },
                            onClick = {
                                selectedModel = model
                                prefs.edit().putString("model", model).apply()
                                menuOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (error != null) {
                Text(
                    text = error ?: "",
                    color = Color(0xFFFF7777),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sift message باش تبدا مع HamAI",
                                color = Color(0xFFB8C8B8)
                            )
                        }
                    }
                }

                items(messages) { msg ->
                    MessageBubble(
                        message = msg,
                        userColor = userBubble,