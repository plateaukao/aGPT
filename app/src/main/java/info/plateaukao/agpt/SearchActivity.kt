package info.plateaukao.agpt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.pixplicity.easyprefs.library.Prefs
import info.plateaukao.agpt.model.GptViewModel
import info.plateaukao.agpt.ui.theme.AGPTTheme

class SearchActivity : AppCompatActivity() {
    private val gptViewModel: GptViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val composeView = findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            val requestMessage by gptViewModel.inputMessage.collectAsState()
            val responseMessage by gptViewModel.responseMessage.collectAsState()

            AGPTTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(BorderStroke(1.dp, Color.Black)),
                    ) {
                        Text(text = requestMessage)
                        Divider()
                        Text(text = responseMessage)
                    }
                }
            }
        }

        setFinishOnTouchOutside(true)

        handleIntent(this.intent)
        overridePendingTransition(0, 0)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun getKeyword(intent: Intent?): String? = intent?.getStringExtra("EXTRA_QUERY")

    private fun handleIntent(intent: Intent?) {
        when {
            getKeyword(intent) != null -> {
                val keyword = getKeyword(intent) ?: return
                queryGPT(keyword)
            }

            intent?.action == Intent.ACTION_PROCESS_TEXT -> {
                val text = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT) ?: return
                queryGPT(text)
            }
        }
    }

    // return true if query is sent
    private fun queryGPT(text: String): Boolean {
        if (Prefs.getString("apiKey", "").isBlank()) {
            Toast.makeText(this, "Please set API key in settings", Toast.LENGTH_SHORT).show()
            return false
        }
        gptViewModel.query(text)
        return true
    }
}