package com.sisl.gpsvideorecorder.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sisl.gpsvideorecorder.PrimaryColor
import com.sisl.gpsvideorecorder.data.RecordingState
import com.sisl.gpsvideorecorder.data.rememberVideoRecorder
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun VideoRecordingScreen() {
    val recorder = rememberVideoRecorder()
    var recordingState by remember { mutableStateOf(RecordingState.IDLE) }

    var showError by remember { mutableStateOf<String?>(null) }

    // Error handling
    LaunchedEffect(recorder) {
        try {
            recorder.initialize {
                println("VideoRecorder Camera initialized successfully")
            }
        } catch (e: Exception) {
            showError = "Camera initialization failed: ${e.message}"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Camera Preview - takes all space above controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .height(450.dp)
        ) {
            recorder.CameraPreview(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        // Error message
        showError?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Bottom Cards Container
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .height(250.dp)
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
                        .background(if (recordingState == RecordingState.RECORDING) PrimaryColor else PrimaryColor),
                    btnName = if (recordingState == RecordingState.RECORDING) "Stop Recording" else "Start Recording",
                    icon = Res.drawable.compose_multiplatform,
                ) {
                    when (recordingState) {
                        RecordingState.IDLE -> {
                            recorder.startRecording()
                            recordingState = RecordingState.RECORDING
                        }

                        RecordingState.RECORDING -> {
                            recorder.stopRecording()
                            recordingState = RecordingState.STOPPED
                        }

                        else -> {
                            // Reset to idle
                            recordingState = RecordingState.IDLE
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                CustomButton(
                    modifier = Modifier
                        .weight(1f)
                        .background(PrimaryColor),
                    btnName = "Steps",
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

                }
                Spacer(modifier = Modifier.width(10.dp))
                CustomButton(
                    modifier = Modifier
                        .weight(1f)
                        .background(PrimaryColor),
                    btnName = "History",
                    icon = Res.drawable.compose_multiplatform
                ) {

                }
            }
        }
    }
}

@Composable
fun CustomButton(modifier: Modifier, btnName: String, icon: DrawableResource, onClick: () -> Unit) {
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