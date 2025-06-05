package com.sisl.gpsvideorecorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.sisl.gpsvideorecorder.presentation.ui.CustomButton
import com.sisl.gpsvideorecorder.presentation.ui.VideoHistoryScreen
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.compose_multiplatform
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var permissionController: MultiPermissionController

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        permissionController = MultiPermissionController(this)

        lifecycleScope.launch {
            val result = permissionController.requestPermissions(
                MultiPermissionController.Permission.LOCATION,
                MultiPermissionController.Permission.CAMERA,
                MultiPermissionController.Permission.NOTIFICATION,
                MultiPermissionController.Permission.AUDIO
            )
            if (result.values.all { it }) {
                // All permissions granted
            } else {
                // Permissions denied, show explanation or close
            }
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    VideoRecordingScreen()
    /* VideoHistoryScreen(
         modifier = Modifier
             .fillMaxSize()
             .background(Color.White)
     )*/
}
