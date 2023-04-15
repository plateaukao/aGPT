package info.plateaukao.agpt.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@OptIn(BetaOpenAI::class)
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

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = messages
        )

        viewModelScope.launch(Dispatchers.IO) {
            val response = openai.chatCompletion(chatCompletionRequest)
            _responseMessage.value = response.choices.first().message?.content ?: ""
        }
    }
}

@OptIn(BetaOpenAI::class)
fun String.toUserMessage() = ChatMessage(
    role = ChatRole.User,
    content = this
)

@OptIn(BetaOpenAI::class)
fun String.toSystemMessage() = ChatMessage(
    role = ChatRole.System,
    content = this
)