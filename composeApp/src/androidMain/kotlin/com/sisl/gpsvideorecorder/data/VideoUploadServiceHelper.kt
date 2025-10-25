package com.sisl.gpsvideorecorder.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.sisl.gpsvideorecorder.data.service.AndroidVideoUploadService
import com.sisl.gpsvideorecorder.presentation.state.UploadVideoState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VideoUploadServiceHelper(private val context: Context) {

    private val _uploadState = MutableStateFlow<UploadVideoState>(UploadVideoState.Idle)
    val uploadState: StateFlow<UploadVideoState> = _uploadState

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "UPLOAD_PROGRESS" -> {
                    val progress = intent.getFloatExtra("progress", 0f)
                    val uploaded = intent.getLongExtra("uploaded", 0L)
                    val total = intent.getLongExtra("total", 0L)
                    _uploadState.value = UploadVideoState.Progress(progress, uploaded, total)
                }

                "UPLOAD_SUCCESS" -> {
                    val filename = intent.getStringExtra("filename") ?: ""
                    val savedPath = intent.getStringExtra("savedPath") ?: ""
                    val message = intent.getStringExtra("message") ?: ""
                    _uploadState.value = UploadVideoState.Success(filename, savedPath, message)
                }

                "UPLOAD_ERROR" -> {
                    val error = intent.getStringExtra("error") ?: "Upload failed"
                    _uploadState.value = UploadVideoState.Error(error)
                }

                "UPLOAD_CANCELLED" -> {
                    _uploadState.value = UploadVideoState.Cancelled
                }
            }
        }
    }

    init {
        // Register broadcast receiver if you want to communicate from service to UI
        val filter = IntentFilter().apply {
            addAction("UPLOAD_PROGRESS")
            addAction("UPLOAD_SUCCESS")
            addAction("UPLOAD_ERROR")
            addAction("UPLOAD_CANCELLED")
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    fun startUpload(videoPath: String, title: String? = null) {
        AndroidVideoUploadService.startUpload(context, videoPath, title)
    }

    fun cancelUpload() {
        AndroidVideoUploadService.cancelUpload(context)
    }

    fun cleanup() {
        context.unregisterReceiver(receiver)
    }
}