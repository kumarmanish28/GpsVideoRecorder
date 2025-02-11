package com.sisl.gpsvideorecorder.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sisl.gpsvideorecorder.viewmodel.LocationViewModel

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val location by viewModel.location.collectAsState()

    Column {
        Button(onClick = { viewModel.fetchLocation() }) {
            Text("Get Location")
        }

        location?.let {
            Text("Latitude: ${it.latitude}, Longitude: ${it.longitude}")
        } ?: Text("Fetching location...")
    }
}
