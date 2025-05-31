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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sisl.gpsvideorecorder.MintGreen
import com.sisl.gpsvideorecorder.data.local.models.DateWisePendingLocation
import gpsvideorecorder.composeapp.generated.resources.Res
import gpsvideorecorder.composeapp.generated.resources.ic_login_bg
import org.jetbrains.compose.resources.painterResource

@Composable
fun VideoHistoryScreen(modifier: Modifier = Modifier) {
    val listOfPendingLocation: List<DateWisePendingLocation> =
        listOf(
            DateWisePendingLocation(1, "234455", 14),
            DateWisePendingLocation(2, "234455", 65),
            DateWisePendingLocation(3, "234455", 3),
            DateWisePendingLocation(4, "234455", 44),
            DateWisePendingLocation(5, "234455", 24),
        )
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

               /* Text(
                    text = "Play",
                    modifier = Modifier.padding(start = 32.dp),
                    fontSize = 12.sp
                )*/

            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(top = 0.dp)

            ) {
                itemsIndexed(listOfPendingLocation) { index, item ->
                    if (index == 0) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .height(0.2.dp)
                                .background(Color.Black)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp, end = 2.dp, bottom = 10.dp, top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "V${item.videoId}", fontSize = 12.sp)
                        Text(text = item.date, fontSize = 12.sp)
                        Text(text = "${item.record}", fontSize = 12.sp)

                        Image(
                            painter = painterResource(Res.drawable.ic_login_bg),
                            contentDescription = "upload data",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    /* item.videoId?.let {
                                         viewModel.updateDataState(it)
                                     }*/
                                }
                        )

                        Image(
                            painter = painterResource(Res.drawable.ic_login_bg),
                            contentDescription = "delete data",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
//                                        onItemClicked(item, "delete")
                                },
                            colorFilter = ColorFilter.tint(Color.Red)
                        )

                       /* Image(
                            painter = painterResource(Res.drawable.ic_login_bg),
                            contentDescription = "Play",
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
//                                        onItemClicked(item, "delete")
                                },
                            colorFilter = ColorFilter.tint(Color.Red)
                        )*/
                    }
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

