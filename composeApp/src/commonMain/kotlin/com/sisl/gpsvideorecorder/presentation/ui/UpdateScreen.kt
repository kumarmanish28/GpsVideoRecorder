package com.sisl.gpsvideorecorder.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sisl.gpsvideorecorder.getPlatform
import com.sisl.gpsvideorecorder.presentation.state.DownloadState
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import org.koin.compose.koinInject

@Composable
fun UpdateScreen(
    navController: NavController,
    viewModel: GpsVideoRecorderViewModel = koinInject(),
) {
    val downloadState by viewModel.downloadState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = downloadState) {
            is DownloadState.Progress -> {
                LinearProgressIndicator(
                    progress = { state.percentage },
                    modifier = Modifier.fillMaxWidth(),
                    color = ProgressIndicatorDefaults.linearColor,
                    trackColor = ProgressIndicatorDefaults.linearTrackColor,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
                Spacer(Modifier.height(8.dp))
                Text("Downloading... ${(state.percentage * 100).toInt()}%")
            }

            is DownloadState.Success -> {
                Text("Download Complete ✅")
                Spacer(Modifier.height(10.dp))
                Button(onClick = { state.bytes?.let {
                    viewModel.installApp(state.bytes)
                }}) {
                    Text("Install App")
                }
            }

            is DownloadState.Error -> {
                Text("Error: ${state.message}", color = Color.Red)
                Spacer(Modifier.height(10.dp))
                Button(onClick = { viewModel.startDownload(getPlatform().name) }) {
                    Text("Retry")
                }
            }

            else -> {
                Button(onClick = { viewModel.startDownload(getPlatform().name) }) {
                    Text("Update App")
                }
            }
        }
    }
}