package com.example.muistikaveri

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class WellnessViewModel : ViewModel() {
    // Viestit ovat privaatteja ja elävät vain tässä ViewModelissa
    val messages = mutableStateListOf<ChatMessage>()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text("Olet Muistikaveri. Keskustelusi potilaan kanssa ovat täysin luottamuksellisia. " +
                    "Puhu empaattisesti ja selkeästi. Älä tallenna tai jaa tietoja eteenpäin.")
        }
    )

    private val chat = generativeModel.startChat()

    init {
        messages.add(ChatMessage("Hei! Olen Muistikaveri. Täällä voit jutella mistä vain luottamuksellisesti.", false))
    }

    fun sendMessage(inputText: String) {
        if (inputText.isBlank()) return
        messages.add(ChatMessage(inputText, true))

        viewModelScope.launch {
            try {
                val response = chat.sendMessage(inputText)
                response.text?.let { messages.add(ChatMessage(it, false)) }
            } catch (e: Exception) {
                messages.add(ChatMessage("Yhteys katkesi hetkeksi. Kokeile uudelleen.", false))
            }
        }
    }
}