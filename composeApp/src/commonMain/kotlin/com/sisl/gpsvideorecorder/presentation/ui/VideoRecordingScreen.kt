package com.sisl.gpsvideorecorder.presentation.ui

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sisl.gpsvideorecorder.MultiPermissionController
import com.sisl.gpsvideorecorder.PrimaryColor
import com.sisl.gpsvideorecorder.Routes
import com.sisl.gpsvideorecorder.presentation.components.recorder.RecordingState
import com.sisl.gpsvideorecorder.presentation.components.recorder.rememberVideoRecorder
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.compose_multiplatform
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalResourceApi::class)
@Composable
fun VideoRecordingScreen(viewModel: GpsVideoRecorderViewModel = koinInject(), onNext: (String) -> Unit) {
    val recorder = rememberVideoRecorder()
    val currentLocation by viewModel.latestLocation.collectAsStateWithLifecycle()
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/video_recorder_anim.json").decodeToString()
        )
    }


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
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomButton(
                            modifier = Modifier
                                .weight(1f)
                                .background(if (viewModel.videoRecordingState.value == RecordingState.RECORDING) Color.Gray else PrimaryColor),
                            btnName = if (viewModel.videoRecordingState.value == RecordingState.RECORDING) "Stop Recording" else "Start Recording",
                            icon = Res.drawable.compose_multiplatform,
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
                            btnName = "${currentLocation?.latitude}",
                            icon = Res.drawable.compose_multiplatform
                        ) {

                        }

                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomButton(
                            modifier = Modifier
                                .weight(1f)
                                .background(PrimaryColor),
                            btnName = "Upload",
                            icon = Res.drawable.compose_multiplatform
                        ) {
                            onNext.invoke(Routes.UPLOAD)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        CustomButton(
                            modifier = Modifier
                                .weight(1f)
                                .background(PrimaryColor),
                            btnName = "History",
                            icon = Res.drawable.compose_multiplatform
                        ) {
                            onNext.invoke(Routes.VIDEO_HISTORY)
                        }
                    }
                }
            }
        }
        if (viewModel.videoRecordingState.value == RecordingState.RECORDING) {
            Image(
                painter = rememberLottiePainter(
                    composition = composition,
                    iterations = Compottie.IterateForever
                ),
                contentDescription = "Lottie animation",
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
            )
        }
    }

}

@Composable
fun CustomButton(
    modifier: Modifier,
    btnName: String,
    icon: DrawableResource,
    onClick: () -> Unit
) {
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
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = btnName,
                color = Color.Green
            )
        }
    }
}
