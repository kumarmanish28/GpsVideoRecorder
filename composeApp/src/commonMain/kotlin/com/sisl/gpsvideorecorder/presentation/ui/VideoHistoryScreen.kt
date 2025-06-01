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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sisl.gpsvideorecorder.MintGreen
import com.sisl.gpsvideorecorder.data.local.models.DateWisePendingLocation
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import com.sisl.gpsvideorecorder.presentation.viewmodels.VideoHistoryScreenViewModel
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun VideoHistoryScreen(
    modifier: Modifier = Modifier,
    videoModel: VideoHistoryScreenViewModel = koinInject()
) {

    val uiState by videoModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, start = 10.dp, end = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title at the top
            Text(
                text = "Location History",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp, bottom = 0.dp, top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "VId", fontSize = 12.sp)

                Text(
                    text = "DateTime",
                    modifier = Modifier.padding(start = 32.dp),
                    fontSize = 12.sp
                )

                Text(
                    text = "Count",
                    modifier = Modifier.padding(start = 32.dp),
                    fontSize = 12.sp
                )

                Text(
                    text = "Upload",
                    modifier = Modifier.padding(start = 32.dp),
                    fontSize = 12.sp
                )

                Text(
                    text = "Delete",
                    modifier = Modifier.padding(start = 32.dp),
                    fontSize = 12.sp
                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
                    .height(0.5.dp)
                    .background(Color.Black)
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.errorMessage!!, color = Color.Red)
                }
            } else if (uiState.videoItemsList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "No Video History Found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp)
                        .weight(1f)
                ) {
                    itemsIndexed(uiState.videoItemsList) { index, item ->
                        VideoHistoryItemRow(
                            item = item,
                            onUploadClicked = { videoModel.onUploadClicked(item.videoId) },
                            onDeleteClicked = { videoModel.onDeleteClicked(item.videoId) }
                        )
                        if (index < uiState.videoItemsList.lastIndex) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(0.2.dp)
                                    .background(Color.Gray)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoHistoryItemRow(
    item: VideoItem,
    onUploadClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 2.dp, end = 2.dp, bottom = 10.dp, top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "V${item.videoId}", fontSize = 12.sp, modifier = Modifier.weight(0.15f))
        Text(text = item.dateTime, fontSize = 12.sp, modifier = Modifier.weight(0.3f))
        Text(
            text = "${item.coordinateCount}",
            fontSize = 12.sp,
            modifier = Modifier.weight(0.15f)
        )

        Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.Center) {
            if (item.isUploaded) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Image(
                    painter = painterResource(Res.drawable.ic_upload), // Replace with your upload icon
                    contentDescription = "Upload Data for V${item.videoId}",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onUploadClicked)
                )
            }
        }

        Box(modifier = Modifier.weight(0.2f), contentAlignment = Alignment.Center) {
            if (item.isDeleted) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Red
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.ic_delete), // Replace with your delete icon
                    contentDescription = "Delete Data for V${item.videoId}",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onDeleteClicked),
                    colorFilter = ColorFilter.tint(Color.Red)
                )
            }
        }
    }
}

