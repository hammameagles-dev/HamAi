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

fun createReply(userText: String): String {
    val text = userText.lowercase()

    return when {
        text.contains("salam") -> "Wa 3alaykom salam."
        text.contains("hello") -> "Hello. How can I help you?"
        text.contains("apk") -> "If GitHub Actions is green, download APK from Artifacts."
        text.contains("merci") -> "Bla jmil."
        text.contains("thanks") -> "You are welcome."
        else -> "I received: " + userText
    }
}