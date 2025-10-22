package com.sisl.gpsvideorecorder.presentation.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sisl.gpsvideorecorder.CremeColor
import com.sisl.gpsvideorecorder.MyAppTypography
import com.sisl.gpsvideorecorder.PrimaryColor
import com.sisl.gpsvideorecorder.Routes
import com.sisl.gpsvideorecorder.data.UploadingState
import com.sisl.gpsvideorecorder.getPlatform
import com.sisl.gpsvideorecorder.presentation.components.MessageDialog
import com.sisl.gpsvideorecorder.presentation.components.recorder.RecordingState
import com.sisl.gpsvideorecorder.presentation.components.recorder.rememberVideoRecorder
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.compose_multiplatform
import gpsvideorecorder.composeapp.generated.resources.current_location
import gpsvideorecorder.composeapp.generated.resources.history
import gpsvideorecorder.composeapp.generated.resources.ic_next
import gpsvideorecorder.composeapp.generated.resources.upload
import gpsvideorecorder.composeapp.generated.resources.video_recording
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import kotlinx.coroutines.delay
import kotlinx.datetime.Month
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@OptIn(ExperimentalResourceApi::class)
@Composable
fun VideoRecordingScreen(
    viewModel: GpsVideoRecorderViewModel = koinInject(),
    onNext: (String) -> Unit,
) {

    val recorder = rememberVideoRecorder(onVideoRecorded = { result ->
        viewModel.onRecordingComplete(result)
    }, onSavingProgress = { progress ->
        viewModel.updateVideoSavingProgress(progress)
    })
    val currentLocation by viewModel.latestLocation.collectAsStateWithLifecycle()
    val videoSavingProgress by viewModel.videoSavingProgress.collectAsState()
    val isVideoSaving by viewModel.isVideoSaving.collectAsState()
    val recordingDuration by viewModel.recordingDuration.collectAsState()

    val formattedDuration by remember(recordingDuration) {
        derivedStateOf {
            val totalSeconds = recordingDuration / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            "${hours.toString().padStart(2, '0')}:${
                minutes.toString().padStart(2, '0')
            }:${seconds.toString().padStart(2, '0')}"
        }
    }

    /*   val composition by rememberLottieComposition {
         LottieCompositionSpec.JsonString(
             Res.readBytes("files/video_recorder_anim.json").decodeToString()
         )
     }*/


    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (viewModel.videoRecordingState.value == RecordingState.RECORDING) {
                        viewModel.stopGpsVideoRecording(recorder)
                    }
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val uploadingState = remember { viewModel.uploadAllPendingCoordinates }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2.5f)
            )
            {
                recorder.CameraPreview(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )

                // Video Saving Progress Overlay
                if (isVideoSaving) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Saving Video... ${videoSavingProgress.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }


                when (uploadingState.value) {
                    UploadingState.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    UploadingState.SUCCESS -> {
                        MessageDialog(
                            modifier = Modifier.height(220.dp).width(280.dp),
                            isSuccessDialog = true, message = "Upload Successfully", onDismiss = {
                                viewModel.uploadAllPendingCoordinates.value =
                                    UploadingState.NULL
                            })
                    }

                    UploadingState.FAILED -> {
                        MessageDialog(
                            modifier = Modifier.height(220.dp).width(280.dp),
                            isSuccessDialog = true, message = "Uploading Failed", onDismiss = {
                                viewModel.uploadAllPendingCoordinates.value =
                                    UploadingState.NULL
                            })
                    }

                    UploadingState.NULL -> {}
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 40.dp, top = 10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Row 2: Start/Stop Recording + Location
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(70.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val fadeDuration = 500
                            val minAlpha = 0.25f
                            val maxAlpha = 1f
                            val infiniteTransition = rememberInfiniteTransition()
                            val alpha: Float =
                                if (viewModel.videoRecordingState.value == RecordingState.RECORDING) {
                                    infiniteTransition.animateFloat(
                                        initialValue = minAlpha,
                                        targetValue = maxAlpha,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(fadeDuration),
                                            repeatMode = RepeatMode.Reverse
                                        )
                                    ).value
                                } else {
                                    1f
                                }

                            CustomButton2(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (viewModel.videoRecordingState.value == RecordingState.RECORDING) Color.Gray else PrimaryColor),
                                btnName = if (viewModel.videoRecordingState.value == RecordingState.RECORDING) "Stop Recording" else "Start Recording",
                                icon = Res.drawable.video_recording,
                                alpha = alpha,
                                msg = if (viewModel.videoRecordingState.value == RecordingState.RECORDING) formattedDuration else "",
                            ) {
                                when (viewModel.videoRecordingState.value) {
                                    RecordingState.STOPPED -> viewModel.startGspVideoRecording(
                                        recorder
                                    )

                                    RecordingState.RECORDING -> viewModel.stopGpsVideoRecording(
                                        recorder
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            CustomButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(PrimaryColor),
                                btnName = "${currentLocation?.latitude ?: "-"}",
                                icon = Res.drawable.current_location
                            ) {
                                // Location action
                            }
                        }
                    }

                    // Row 2: Upload + History
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(70.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CustomButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(PrimaryColor),
                                btnName = "Upload",
                                icon = Res.drawable.upload,
                            ) {
                                viewModel.onUploadClicked()
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            CustomButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(PrimaryColor),
                                btnName = "History",
                                icon = Res.drawable.history
                            ) {
                                if (viewModel.videoRecordingState.value == RecordingState.RECORDING) {
                                    viewModel.stopGpsVideoRecording(recorder)
                                }
                                if (isVideoSaving) {
                                    if (videoSavingProgress.toInt() >= 100) {
                                        onNext.invoke(Routes.VIDEO_HISTORY)
                                    }
                                } else {
                                    // If no video is saving, navigate immediately
                                    onNext.invoke(Routes.VIDEO_HISTORY)
                                }
                            }
                        }
                    }

                    // Row 3: Update APK + Logout
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(70.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CustomButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(PrimaryColor),
                                btnName = "Update APK",
                                isBtnEnable = false,
                                alpha = 0.5f,
                                icon = Res.drawable.ic_next, // Use appropriate icon
                            ) {
                                viewModel.onUpdateApkClicked()
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            CustomButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(PrimaryColor),
                                btnName = "Logout",
                                icon = Res.drawable.ic_next // Use appropriate icon
                            ) {
                                viewModel.onLogoutClicked {
                                    onNext.invoke(Routes.LOGIN)
                                }
                            }
                        }
                    }

                    // Add extra space at the bottom
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier,
    isBtnEnable: Boolean = true,
    btnName: String,
    icon: DrawableResource,
    alpha: Float = 1f,
    onClick: () -> Unit,
) {
    /*   val platFormName = remember { getPlatform().name }
           .height(if (platFormName == "iOS") 160.dp else 120.dp),*/
    Card(
        modifier = modifier.background(Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize().clickable(enabled = isBtnEnable) {
                    onClick.invoke()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
                    .graphicsLayer { this.alpha = alpha },
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = btnName ?: "-",
                color = Color.White,
                fontFamily = MyAppTypography().labelMedium.fontFamily
            )
        }
    }
}

@Composable
fun CustomButton2(
    modifier: Modifier,
    isBtnEnable: Boolean = true,
    msg: String = "",
    btnName: String,
    icon: DrawableResource,
    alpha: Float = 1f,
    onClick: () -> Unit,
) {
    /*   val platFormName = remember { getPlatform().name }
           .height(if (platFormName == "iOS") 160.dp else 120.dp),*/
    Card(
        modifier = modifier.background(Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize().clickable(enabled = isBtnEnable) {
                    onClick.invoke()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                        .graphicsLayer { this.alpha = alpha },
                    tint = Color.White
                )
                if (msg.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = msg,
                        color = Color.White,
                        fontFamily = MyAppTypography().labelMedium.fontFamily
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = btnName ?: "-",
                color = Color.White,
                fontFamily = MyAppTypography().labelMedium.fontFamily
            )
        }
    }
}



