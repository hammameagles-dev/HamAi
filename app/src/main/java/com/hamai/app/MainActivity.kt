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

        Spacer(modifier = Modifier.height(8.dp))

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
            verticalAlignment = Alignment.CenterVertically
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
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Clear")
            }
        }
    }
}

fun createBotReply(userText: String): String {
    val text = userText.lowercase()

    return when {
        text.contains("salam") || text.contains("hello") || text.contains("hi") -> {
            "Salam. Kifach n9der n3awnek?"
        }

        text.contains("apk") -> {
            "To build APK, run GitHub Actions. If it is green, download the APK from Artifacts."
        }

        text.contains("chkon") || text.contains("who") -> {
            "Ana HamAI, simple chat app built with Kotlin and Jetpack Compose."
        }

        text.contains("merci") || text.contains("thanks") -> {
            "Bla jmil."
        }

        else -> {
            "Fhemtk. This is a simple version. Later we can add real AI API."
        }
    }
}
