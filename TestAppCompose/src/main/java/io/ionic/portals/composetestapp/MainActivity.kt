package io.ionic.portals.composetestapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.ionic.portals.Portal
import io.ionic.portals.PortalView
import io.ionic.portals.composetestapp.ui.theme.IonicPortalsTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IonicPortalsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    loadPortal(Portal("testportal"))
                }
            }
        }
    }
}

@Composable
fun loadPortal(portal: Portal) {
    AndroidView(factory = {
        PortalView(it, portal)
    })
}