package com.sisl.gpsvideorecorder.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.sisl.gpsvideorecorder.PrimaryColor
import com.sisl.gpsvideorecorder.Routes
import com.sisl.gpsvideorecorder.getPlatform
import com.sisl.gpsvideorecorder.presentation.components.RedButtonCTA
import com.sisl.gpsvideorecorder.presentation.state.DownloadState
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.ic_download_apk
import gpsvideorecorder.composeapp.generated.resources.ic_next
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
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = downloadState) {
            is DownloadState.Loading -> {
                Text("Preparing download...")
                CircularProgressIndicator()
            }
            is DownloadState.Progress -> {
                Text("Downloading... ${(state.percentage * 100).toInt()}%")
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                progress = { state.percentage },
                modifier = Modifier.fillMaxWidth(),
                color = PrimaryColor,
                trackColor = PrimaryColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }
            is DownloadState.Success -> {
                Text("Download completed!", color = PrimaryColor)
                Spacer(modifier = Modifier.height(2.dp))
                RedButtonCTA(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 30.dp)
                        .height(48.dp),
                    onButtonClicked = {
                        state.filePath?.let {
                            viewModel.installApp(it)
                        }
                    },
                    buttonText = "Install Now",
                    drawableIcon = Res.drawable.ic_download_apk
                )
            }
            is DownloadState.Error -> {
                Text("❌ ${state.message}", color = Color.Red)
                Spacer(modifier = Modifier.height(2.dp))
                RedButtonCTA(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 30.dp)
                        .height(48.dp),
                    onButtonClicked = {
                        viewModel.resetDownloadState()
                    },
                    buttonText = "Try Again",
                    drawableIcon = Res.drawable.ic_download_apk
                )

            }
            null -> {
                RedButtonCTA(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 30.dp)
                        .height(48.dp),
                    onButtonClicked = {
                        viewModel.startDownload("android")
                    },
                    buttonText = "Download Update",
                    drawableIcon = Res.drawable.ic_download_apk
                )
            }
        }
    }
}