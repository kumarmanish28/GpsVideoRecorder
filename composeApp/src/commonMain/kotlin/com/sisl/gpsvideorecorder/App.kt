package com.sisl.gpsvideorecorder

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
    val defaultEnterTransitionDuration = 300
    val defaultExitTransitionDuration = 200

    MyTheme {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH
        ) {
            composable(
                Routes.SPLASH, enterTransition = {
                    // When navigating TO VideoHistoryScreen
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from the right
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                exitTransition = {
                    // When navigating AWAY FROM VideoHistoryScreen
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to the left
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                },
                popEnterTransition = {
                    // When returning TO VideoHistoryScreen via back press/pop
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth }, // Slide in from the left
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                popExitTransition = {
                    // When VideoHistoryScreen is popped FROM the back stack
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Slide out to the right
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                }) {
                SplashScreen {
                    navController.navigate(Routes.LOGIN)
                }
            }
            composable(
                Routes.LOGIN, enterTransition = {
                    // When navigating TO VideoHistoryScreen
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from the right
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                exitTransition = {
                    // When navigating AWAY FROM VideoHistoryScreen
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to the left
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                },
                popEnterTransition = {
                    // When returning TO VideoHistoryScreen via back press/pop
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth }, // Slide in from the left
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                popExitTransition = {
                    // When VideoHistoryScreen is popped FROM the back stack
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Slide out to the right
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                }) {
                LoginScreen {
                    navController.navigate(Routes.VIDEO_RECORDING) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            }
            composable(
                Routes.VIDEO_RECORDING,
                enterTransition = {
                    // When navigating TO VideoRecordingScreen
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from the right
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                exitTransition = {
                    // When navigating AWAY FROM VideoRecordingScreen (e.g., to VideoHistory)
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to the left
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                },
                popEnterTransition = {
                    // When returning TO VideoRecordingScreen via back press/pop
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth }, // Slide in from the left
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                popExitTransition = {
                    // When VideoRecordingScreen is popped FROM the back stack
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Slide out to the right
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                }) {
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
            composable(
                Routes.VIDEO_HISTORY,
                enterTransition = {
                    // When navigating TO VideoHistoryScreen
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth }, // Slide in from the right
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                exitTransition = {
                    // When navigating AWAY FROM VideoHistoryScreen
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth }, // Slide out to the left
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                },
                popEnterTransition = {
                    // When returning TO VideoHistoryScreen via back press/pop
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth }, // Slide in from the left
                        animationSpec = tween(defaultEnterTransitionDuration)
                    ) + fadeIn(animationSpec = tween(defaultEnterTransitionDuration))
                },
                popExitTransition = {
                    // When VideoHistoryScreen is popped FROM the back stack
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth }, // Slide out to the right
                        animationSpec = tween(defaultExitTransitionDuration)
                    ) + fadeOut(animationSpec = tween(defaultExitTransitionDuration))
                }) {
                VideoHistoryScreen(navController)
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