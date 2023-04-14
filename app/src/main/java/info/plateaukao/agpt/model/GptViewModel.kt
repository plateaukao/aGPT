package info.plateaukao.agpt.model

import android.content.ContextWrapper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjcrafter.openai.OpenAI
import com.cjcrafter.openai.chat.ChatMessage.Companion.toSystemMessage
import com.cjcrafter.openai.chat.ChatMessage.Companion.toUserMessage
import com.cjcrafter.openai.chat.ChatRequest
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GptViewModel : ViewModel() {
    private val openai: OpenAI by lazy { OpenAI(Prefs.getString("apiKey", "")) }

    private val _responseMessage = MutableStateFlow("")
    val responseMessage: StateFlow<String> = _responseMessage.asStateFlow()

    private val _inputMessage = MutableStateFlow("")
    val inputMessage: StateFlow<String> = _inputMessage.asStateFlow()

    fun query(userMessage: String) {
        _inputMessage.value = userMessage

        val prompt =
            "You are a good interpreter.".toSystemMessage()
        val messages = mutableListOf(prompt).apply {
            add("Translate following content into Traditional Chinese: $userMessage".toUserMessage())
        }

        val request = ChatRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messages).build()

        viewModelScope.launch(Dispatchers.IO) {
            val response = openai.createChatCompletion(request)
            _responseMessage.value = response.choices.first().message.content
        }
    }
}