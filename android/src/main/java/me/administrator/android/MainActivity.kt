package me.administrator.android

import android.os.Bundle
import android.view.Surface
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.elong.lightboat.ui.AndroidWebView
import com.elong.lightboat.ui.WebPage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
//                App()
                Box {
                    WebPage(webview = @Composable { url, modifier ->
                        AndroidWebView(url, modifier)
                    })
//                    download()
                }

            }
        }
        holder = DownloadContextHolder()
        holder?.create(this)
    }
}

var holder: DownloadContextHolder? = null
@Composable
fun download() {
    Button(
        modifier = Modifier.background(Color.Blue),
        onClick = {
            holder?.download()
        }
    ) {
        Text("下载")
    }
}