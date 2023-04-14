package info.plateaukao.agpt

import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pixplicity.easyprefs.library.Prefs
import info.plateaukao.agpt.model.GptViewModel
import info.plateaukao.agpt.ui.theme.AGPTTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val gptViewModel: GptViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val gptKey = remember { mutableStateOf(Prefs.getString("apiKey", "")) }
            val requestMessage = remember { mutableStateOf("") }
            val responseMessage by gptViewModel.responseMessage.collectAsState()
            AGPTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.wrapContentHeight(),
                            value = gptKey.value,
                            onValueChange = {
                                gptKey.value = it
                                Prefs.putString("apiKey", it)
                            },
                        )
                        OutlinedTextField(
                            modifier = Modifier.wrapContentHeight(),
                            value = requestMessage.value,
                            onValueChange = {
                                requestMessage.value = it
                            },
                        )
                        Button(onClick = { gptViewModel.query(requestMessage.value) }) {
                            Text("Query")
                        }
                        Text(text = responseMessage, modifier = Modifier.wrapContentHeight())
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AGPTTheme {
        Greeting("Android")
    }
}