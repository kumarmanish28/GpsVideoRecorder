package com.sisl.gpsvideorecorder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sisl.gpsvideorecorder.CameraPreview
import com.sisl.gpsvideorecorder.getVideoRecorder
import com.sisl.gpsvideorecorder.permission.PermissionHandler

@Composable
fun HomeScreen(context: Any?, permissionHandler: PermissionHandler, permission: List<String>) {

    val recorder = remember { getVideoRecorder(context) }
    var isRecording by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Black)
        ) {
            CameraPreview(recorder, context)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Last Capture Time:  ${"00:00:00"}",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(text = "Speed:  ${0}(Km/h)")
            Text(text = "Latitude: ${"0.00000000"}")
            Text(text = "Longitude: ${"0.00000000"}")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 5.dp, 5.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        permissionHandler.requestPermissions(
                            permissions = permission
                        ) { granted, denied ->
                            if (granted) {
                                if (isRecording) {
                                    recorder.stopRecording()
                                } else {
                                    recorder.startRecording()
                                }
                                isRecording = !isRecording
                            } else {
                                //todo
                            }
                        }
                    },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else Color.Green
                    )
                ) {
                    Text(
                        text = if (isRecording) "Stop Recording" else "Start Recording",
                        color = if(isRecording) Color.White else Color.Black // Change text color
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) {
                    Text(
                        text = "${0 ?: "0"}",
                        textAlign = TextAlign.End,
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 0.dp, 5.dp, 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {

                    },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.weight(1f),
                    enabled = !isRecording,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Green
                    )
                ) { Text(text = "Upload Coordinates", color = Color.Black) }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = {
                        //viewModel.clearAllData()
                    },
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text(
                        text = "History", color = Color.Black
                    )
                }
            }
        }
    }
}
