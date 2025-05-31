package com.sisl.gpsvideorecorder

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sisl.gpsvideorecorder.presentation.ui.LoginScreen
import com.sisl.gpsvideorecorder.presentation.ui.SplashScreen
import com.sisl.gpsvideorecorder.presentation.ui.VideoHistoryScreen
import com.sisl.gpsvideorecorder.presentation.ui.VideoRecordingScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    MyTheme {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH
        ) {
            composable(Routes.SPLASH) {
                SplashScreen {
                    navController.navigate(Routes.LOGIN)
                }
            }
            composable(Routes.LOGIN) {
                LoginScreen {
                    navController.navigate(Routes.VIDEO_RECORDING) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            }
            composable(Routes.VIDEO_RECORDING) {
                VideoRecordingScreen {
                    when (it) {
                        Routes.VIDEO_HISTORY -> {
                            navController.navigate(Routes.VIDEO_HISTORY)
                        }

                        Routes.UPLOAD -> {
                            navController.navigate(Routes.UPLOAD)
                        }
                    }

                }
            }
            composable(Routes.VIDEO_HISTORY) {
                VideoHistoryScreen()
            }
        }
    }

}


object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val VIDEO_RECORDING = "video_recording"
    const val VIDEO_HISTORY = "VIDEO_HISTORY"
    const val UPLOAD = "UPLOAD"
}