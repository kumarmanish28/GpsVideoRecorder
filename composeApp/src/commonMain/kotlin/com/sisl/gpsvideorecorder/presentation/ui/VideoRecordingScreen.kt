package com.sisl.gpsvideorecorder.presentation.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
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
import gpsvideorecorder.composeapp.generated.resources.upload
import gpsvideorecorder.composeapp.generated.resources.video_recording
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
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
    onNext: (String) -> Unit
) {
    val recorder = rememberVideoRecorder(onVideoRecorded = { result ->
        viewModel.onRecordingComplete(result)
    })
    val currentLocation by viewModel.latestLocation.collectAsStateWithLifecycle()
    /*   val composition by rememberLottieComposition {
         LottieCompositionSpec.JsonString(
             Res.readBytes("files/video_recorder_anim.json").decodeToString()
         )
     }*/

    val uploadingState = remember { viewModel.uploadAllPendingCoordinates }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2.4f)
            ) {
                recorder.CameraPreview(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )

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
            ) {
                // Bottom Cards Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 40.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val fadeDuration = 500 // 1 second for each fade in/out
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


                        CustomButton(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (viewModel.videoRecordingState.value == RecordingState.RECORDING) Color.Gray else PrimaryColor),
                            btnName = if (viewModel.videoRecordingState.value == RecordingState.RECORDING) "Stop Recording" else "Start Recording",
                            icon = Res.drawable.video_recording,
                            alpha = alpha
                        ) {
                            when (viewModel.videoRecordingState.value) {
                                RecordingState.STOPPED -> {
                                    viewModel.startGspVideoRecording(recorder)
                                }

                                RecordingState.RECORDING -> {
                                    viewModel.stopGpsVideoRecording(recorder)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        CustomButton(
                            modifier = Modifier
                                .weight(1f)
                                .background(PrimaryColor),
                            btnName = "${currentLocation?.latitude?:"-"}",
                            icon = Res.drawable.current_location
                        ) {

                        }

                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 5.dp),
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
                            onNext.invoke(Routes.VIDEO_HISTORY)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier,
    btnName: String,
    icon: DrawableResource,
    alpha: Float = 1f,
    onClick: () -> Unit
) {
    /*   val platFormName = remember { getPlatform().name }
           .height(if (platFormName == "iOS") 160.dp else 120.dp),*/
    Card(
        modifier = modifier.background(Color.White),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize().clickable {
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
                text = btnName?:"-",
                color = Color.Green,
                fontFamily = MyAppTypography().labelMedium.fontFamily
            )
        }
    }
}

