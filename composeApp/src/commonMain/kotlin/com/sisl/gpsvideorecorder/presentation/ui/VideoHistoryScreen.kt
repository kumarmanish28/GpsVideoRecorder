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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sisl.gpsvideorecorder.MintGreen
import com.sisl.gpsvideorecorder.MyAppTypography
import com.sisl.gpsvideorecorder.data.local.models.DateWisePendingLocation
import com.sisl.gpsvideorecorder.getPlatform
import com.sisl.gpsvideorecorder.presentation.components.MessageDialog
import com.sisl.gpsvideorecorder.presentation.state.UploadVideoState
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import com.sisl.gpsvideorecorder.presentation.viewmodels.VideoHistoryScreenViewModel
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun VideoHistoryScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    videoModel: VideoHistoryScreenViewModel = koinInject()
) {

    val uiState by videoModel.uiState.collectAsState()

    val uploadState by videoModel.uploadVideoState.collectAsState() // Get upload state
    val platFormName = remember { getPlatform() }

    // State to control which video is being uploaded and show dialog
    var uploadingVideoId by remember { mutableStateOf<Long?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uploadState) {
        println("🔄 Upload state changed: $uploadState")
        when (uploadState) {
            is UploadVideoState.Preparing -> {
                println("📱 Setting showUploadDialog to TRUE (Preparing)")
                showUploadDialog = true
            }
            is UploadVideoState.Uploading -> {
                println("📱 Setting showUploadDialog to TRUE (Progress)")
                showUploadDialog = true
            }
            is UploadVideoState.Success -> {
                println("📱 Keeping dialog open (Success)")
                showUploadDialog = true
            }
            is UploadVideoState.Error -> {
                println("📱 Keeping dialog open (Error)")
                showUploadDialog = true
            }
            else -> {
                println("📱 No change to dialog state")
            }
        }
    }


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(
                vertical = if (platFormName.name == "iOS") 24.dp else 48.dp, horizontal = 10.dp
            ), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(36.dp).padding(start = 8.dp, bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back), contentDescription = "Back"
                    )
                }
                Text(
                    text = "Location History",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MyAppTypography().labelLarge.fontFamily
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp, bottom = 0.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VId",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    fontFamily = MyAppTypography().labelMedium.fontFamily,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "DateTime",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    fontFamily = MyAppTypography().labelMedium.fontFamily,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Count",
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    fontFamily = MyAppTypography().labelMedium.fontFamily,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Action",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f),
                    fontFamily = MyAppTypography().labelMedium.fontFamily,
                    textAlign = TextAlign.Center
                )

            }

            Spacer(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp).height(0.5.dp)
                    .background(Color.Black)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            } else if (uiState.videoItemsList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Video History Found.",
                        fontFamily = MyAppTypography().bodyLarge.fontFamily
                    )
                }
            } else {
                if (uiState.successMessage != null) {
                    MessageDialog(
                        modifier = Modifier.height(220.dp).width(280.dp),
                        isSuccessDialog = true,
                        message = uiState.successMessage!!,
                        onDismiss = {
                            videoModel.clearSuccessMessage() // Clear dialog after dismiss
                        })
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp).weight(1f)
                ) {
                    itemsIndexed(uiState.videoItemsList) { index, item ->
                        VideoHistoryItemRow(item = item, onUploadCoordinatesClicked = {
                            videoModel.onUploadClicked(item.videoId)
                        }, onUploadVideoClicked = {
                            uploadingVideoId = item.videoId
                            videoModel.onUploadVideoClicked(item.videoId)
                        }, onDeleteClicked = {
                            videoModel.onDeleteClicked(item.videoId)
                        })
                        if (index < uiState.videoItemsList.lastIndex) {
                            Spacer(
                                modifier = Modifier.fillMaxWidth().height(0.2.dp)
                                    .background(Color.Gray)
                            )
                        }
                    }
                }
            }
        }
    }

    // Video Upload Dialog
    if (showUploadDialog && uploadingVideoId != null) {
        VideoUploadDialog(
            videoId = uploadingVideoId!!,
            uploadState = uploadState,
            onDismissRequest = {
                showUploadDialog = false
                uploadingVideoId = null
                videoModel.resetState()
            },
            onCancelUpload = {
                videoModel.cancelUpload()
            },
            onRetryUpload = {
                uploadingVideoId?.let { videoId ->
                    videoModel.onUploadVideoClicked(videoId)
                }
            }
        )
    }
}

@Composable
fun VideoHistoryItemRow(
    item: VideoItem,
    onUploadCoordinatesClicked: () -> Unit,
    onUploadVideoClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 10.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "V${item.videoId}",
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            fontFamily = MyAppTypography().labelMedium.fontFamily,
            textAlign = TextAlign.Center
        )
        Text(
            text = item.dateTime,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            fontFamily = MyAppTypography().labelMedium.fontFamily,
            textAlign = TextAlign.Center
        )
        Text(
            text = "${item.coordinateCount}",
            fontSize = 12.sp,
            modifier = Modifier.weight(1f),
            fontFamily = MyAppTypography().labelMedium.fontFamily,
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Menu Icon
            Image(
                painter = painterResource(Res.drawable.ic_menu),
                contentDescription = "Menu Options",
                modifier = Modifier
                    .size(20.dp)
                    .clickable { expanded = true }
            )

            // Enhanced Dropdown Menu with Icons
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .width(150.dp)
            ) {
                // Upload Location Option
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onUploadCoordinatesClicked()
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_upload_location),
                                contentDescription = "Upload Location",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upload Location",
                                fontSize = 14.sp
                            )
                        }
                    }
                )

                // Upload Video Option
                DropdownMenuItem(
                    modifier = Modifier,
                    onClick = {
                        expanded = false
                        onUploadVideoClicked()
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_upload_video),
                                contentDescription = "Upload Video",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upload Video",
                                fontSize = 14.sp
                            )
                        }
                    }
                )

                // Delete Location Option
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onDeleteClicked()
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_delete),
                                contentDescription = "Delete Location",
                                modifier = Modifier.size(16.dp),
                                colorFilter = ColorFilter.tint(Color.Red)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delete Location",
                                fontSize = 14.sp,
                                color = Color.Red
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun VideoUploadDialog(
    videoId: Long,
    uploadState: UploadVideoState,
    onDismissRequest: () -> Unit,
    onCancelUpload: () -> Unit,
    onRetryUpload: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // Only allow dismissal if not actively uploading
            when (uploadState) {
                is UploadVideoState.Uploading -> {
                    // Show confirmation or auto-cancel
                    onCancelUpload()
                    onDismissRequest()
                }

                else -> onDismissRequest()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = uploadState !is UploadVideoState.Uploading,
            dismissOnClickOutside = uploadState !is UploadVideoState.Uploading
        ),
        title = {
            Text(
                text = when (uploadState) {
                    is UploadVideoState.Uploading -> "Uploading Video V$videoId"
                    is UploadVideoState.Success -> "Upload Complete"
                    is UploadVideoState.Error -> "Upload Failed"
                    is UploadVideoState.Cancelled -> "Upload Cancelled"
                    else -> "Video Upload"
                }
            )
        },
        text = {
            when (uploadState) {
                UploadVideoState.Idle -> {
                    UploadIdleView()
                }

                UploadVideoState.Preparing -> {
                    UploadPreparingView()
                }

                is UploadVideoState.Uploading -> {
                    UploadProgressView(
                        progress = uploadState.progress,
                        uploadedBytes = uploadState.uploadedBytes,
                        totalBytes = uploadState.totalBytes
                    )
                }

                is UploadVideoState.Success -> {
                    UploadSuccessView(
                        filename = uploadState.filename,
                        savedPath = uploadState.savedPath,
                        message = uploadState.message
                    )
                }

                is UploadVideoState.Error -> {
                    UploadErrorView(errorMessage = uploadState.message)
                }

                UploadVideoState.Cancelled -> {
                    UploadCancelledView()
                }

                UploadVideoState.Finalizing -> {


                }
                UploadVideoState.Initiating -> {

                }
            }
        },
        confirmButton = {
            when (uploadState) {
                is UploadVideoState.Uploading -> {
                    Button(
                        onClick = {
                            onCancelUpload()
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancel Upload")
                    }
                }

                is UploadVideoState.Success -> {
                    Button(onClick = onDismissRequest) {
                        Text("Done")
                    }
                }

                is UploadVideoState.Error -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onRetryUpload,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Text("Retry")
                        }
                        Button(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Close")
                        }
                    }
                }

                UploadVideoState.Cancelled -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onRetryUpload,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
                        ) {
                            Text("Try Again")
                        }
                        Button(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                        ) {
                            Text("Close")
                        }
                    }
                }

                else -> {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    )
}

// Dialog content views (without buttons)
@Composable
private fun UploadIdleView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text("Initializing upload...")
    }
}

@Composable
private fun UploadPreparingView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text("Preparing video for upload...")
    }
}

@Composable
private fun UploadProgressView(
    progress: Float,
    uploadedBytes: Long,
    totalBytes: Long
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color.Blue,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )

        Text(
            "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            "${formatBytes(uploadedBytes)} / ${formatBytes(totalBytes)}",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun UploadSuccessView(
    filename: String,
    savedPath: String,
    message: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            modifier = Modifier.size(48.dp),
            tint = Color.Green
        )

        Text(
            "Upload completed successfully!",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        if (filename.isNotEmpty()) {
            Text(
                "File: $filename",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun UploadErrorView(errorMessage: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = Color.Red
        )

        Text(
            errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Red
        )
    }
}

@Composable
private fun UploadCancelledView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cancelled",
            modifier = Modifier.size(48.dp),
            tint = Color.Yellow
        )

        Text(
            "Upload was cancelled",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Yellow
        )
    }
}

// commonMain
private fun formatBytes(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = bytes.toDouble()
    var unitIndex = 0

    while (size > 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }

    // Manual formatting with 1 decimal place
    val integerPart = size.toInt()
    val decimalPart = ((size - integerPart) * 10).toInt()

    return if (decimalPart > 0) {
        "$integerPart.$decimalPart ${units[unitIndex]}"
    } else {
        "$integerPart ${units[unitIndex]}"
    }
}
