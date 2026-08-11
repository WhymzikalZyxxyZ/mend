package xyz.zyxwonderland.mend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.zyxwonderland.mend.ui.theme.MendTheme
import xyz.zyxwonderland.mend.ui.update.UpdateBanner
import xyz.zyxwonderland.mend.update.UpdateViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MendTheme {
                val updateViewModel: UpdateViewModel = viewModel()
                val updateInfo by updateViewModel.updateInfo.collectAsState()
                val context = LocalContext.current

                Column(modifier = Modifier.fillMaxSize()) {
                    updateInfo?.let { info ->
                        UpdateBanner(
                            info = info,
                            onView = {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(info.htmlUrl)))
                            },
                            onDismiss = updateViewModel::dismiss,
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("MEND — coming soon")
                    }
                }
            }
        }
    }
}
