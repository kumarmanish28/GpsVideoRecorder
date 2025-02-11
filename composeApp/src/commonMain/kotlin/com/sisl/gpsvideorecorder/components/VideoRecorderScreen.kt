package com.sisl.gpsvideorecorder.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sisl.gpsvideorecorder.getVideoRecorder

@Composable
fun VideoRecorderScreen(context: Any?) {
    val recorder = remember { getVideoRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            if (isRecording) {
                recorder.stopRecording()
            } else {
                recorder.startRecording()
            }
            isRecording = !isRecording
        }) {
            Text(if (isRecording) "Stop Recording" else "Start Recording")
        }
    }
}