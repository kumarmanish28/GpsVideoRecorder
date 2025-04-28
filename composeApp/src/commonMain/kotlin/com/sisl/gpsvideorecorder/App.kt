package com.sisl.gpsvideorecorder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sisl.gpsvideorecorder.ui.LoginScreen
import com.sisl.gpsvideorecorder.ui.SplashScreen
import com.sisl.gpsvideorecorder.ui.VideoRecordingScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    VideoRecordingScreen()
   /* var currentScreen by remember { mutableStateOf(Screen.Splash) }

    MyTheme {
        when (currentScreen) {
            Screen.Splash -> {
                SplashScreen {
                    currentScreen = Screen.Login
                }
            }
            Screen.Login -> {
                LoginScreen {
                    // On successful login
                    currentScreen = Screen.VideoRecording
                }
            }
            Screen.VideoRecording -> {
                VideoRecordingScreen()
            }
        }
    }*/
}

private enum class Screen {
    Splash,
    Login,
    VideoRecording
}