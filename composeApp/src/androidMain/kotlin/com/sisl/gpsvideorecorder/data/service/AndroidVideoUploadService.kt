package com.sisl.gpsvideorecorder.data.service


import android.app.*
import android.content.*
import android.os.*
import androidx.core.app.NotificationCompat
import com.sisl.gpsvideorecorder.data.remote.api.VideoUploadApiServiceImpl
import com.sisl.gpsvideorecorder.presentation.state.UploadVideoState
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class AndroidVideoUploadService : Service() {
    private var isUploading = false
    private var currentJob: Job? = null
    private lateinit var uploadApiService: VideoUploadApiServiceImpl

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "video_upload_channel"
        const val ACTION_START_UPLOAD = "START_UPLOAD"
        const val ACTION_CANCEL_UPLOAD = "CANCEL_UPLOAD"
        const val EXTRA_VIDEO_PATH = "video_path"
        const val EXTRA_TITLE = "title"

        fun startUpload(context: Context, videoPath: String, title: String? = null) {
            val intent = Intent(context, AndroidVideoUploadService::class.java).apply {
                action = ACTION_START_UPLOAD
                putExtra(EXTRA_VIDEO_PATH, videoPath)
                putExtra(EXTRA_TITLE, title)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun cancelUpload(context: Context) {
            val intent = Intent(context, AndroidVideoUploadService::class.java).apply {
                action = ACTION_CANCEL_UPLOAD
            }
            context.startService(intent)
        }
    }

    private val _uploadState = MutableStateFlow<UploadVideoState>(UploadVideoState.Idle)
    val uploadState: StateFlow<UploadVideoState> = _uploadState

    override fun onCreate() {
        super.onCreate()
        // Initialize the API service
        uploadApiService = VideoUploadApiServiceImpl(HttpClient())
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_UPLOAD -> {
                val videoPath = intent.getStringExtra(EXTRA_VIDEO_PATH) ?: return START_NOT_STICKY
                val title = intent.getStringExtra(EXTRA_TITLE)
                startUpload(videoPath, title)
            }

            ACTION_CANCEL_UPLOAD -> {
                cancelUpload()
            }
        }
        return START_NOT_STICKY
    }

    private fun startUpload(videoPath: String, title: String?) {
        if (isUploading) return

        isUploading = true
        _uploadState.value = UploadVideoState.Preparing

        // Start foreground service
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createUploadNotification(0f, "Preparing upload..."))

        currentJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = File(videoPath)
                if (!file.exists()) {
                    _uploadState.value = UploadVideoState.Error("Video file not found")
                    stopForeground(true)
                    return@launch
                }

                val totalBytes = file.length()
                val fileName = file.name

                println("🚀 Starting upload: $fileName, Size: $totalBytes bytes")

                // Read file bytes
//                val videoBytes = file.readBytes()
//                if (videoBytes.isEmpty()) {
//                    _uploadState.value = UploadVideoState.Error("Video file is empty")
//                    stopForeground(true)
//                    return@launch
//                }
//
//                println("📁 File read successfully: ${videoBytes.size} bytes")
//
//                // Start actual upload with progress tracking
//                uploadApiService.uploadVideo(
//                    videoBytes = videoBytes,
//                    fileName = fileName,
//                    onProgress = { progress ->
//                        val uploadedBytes = (progress * totalBytes).toLong()
//                        _uploadState.value =
//                            UploadVideoState.Progress(progress, uploadedBytes, totalBytes)
//
//                        // Update notification
//                        updateNotification(
//                            progress = progress,
//                            status = "Uploading... ${(progress * 100).toInt()}%"
//                        )
//                    }
//                ).let { response ->
//                    // Upload successful
//                    _uploadState.value = UploadVideoState.Success(
//                        filename = response.filename ?: "",
//                        savedPath = response.saved_path ?: "",
//                        message = response.message ?: ""
//                    )
//
//                    // Show success notification
//                    updateNotification(
//                        progress = 1.0f,
//                        status = "Upload completed!",
//                        isCompleted = true
//                    )
//
//                    // Stop foreground after delay to show success
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(3000) // Show success for 3 seconds
//                        stopForeground(true)
//                    }
//                }

            } catch (e: CancellationException) {
                _uploadState.value = UploadVideoState.Cancelled
                updateNotification(progress = 0f, status = "Upload cancelled", isCompleted = true)
                stopForeground(true)
            } catch (e: Exception) {
                _uploadState.value = UploadVideoState.Error(e.message ?: "Upload failed")
                println("❌ Upload error: ${e.message}")
                e.printStackTrace()
                updateNotification(progress = 0f, status = "Upload failed", isCompleted = true)
                stopForeground(true)
            } finally {
                isUploading = false
            }
        }
    }

    private fun cancelUpload() {
        isUploading = false
        currentJob?.cancel()
        _uploadState.value = UploadVideoState.Cancelled
        updateNotification(progress = 0f, status = "Upload cancelled", isCompleted = true)
        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video Upload",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows video upload progress"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createUploadNotification(
        progress: Float,
        status: String,
        isCompleted: Boolean = false
    ): Notification {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Video Upload")
            .setContentText(status)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setOngoing(!isCompleted)
            .setOnlyAlertOnce(true)
            .setAutoCancel(isCompleted)

        if (!isCompleted) {
            // Show progress bar for ongoing upload
            builder.setProgress(100, (progress * 100).toInt(), false)
                .addAction(
                    android.R.drawable.ic_menu_close_clear_cancel,
                    "Cancel",
                    getCancelPendingIntent()
                )
        } else {
            // Remove progress bar for completed/failed uploads
            builder.setProgress(0, 0, false)
        }

        return builder.build()
    }

    private fun updateNotification(
        progress: Float,
        status: String,
        isCompleted: Boolean = false
    ) {
        val notification = createUploadNotification(progress, status, isCompleted)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getCancelPendingIntent(): PendingIntent {
        val intent = Intent(this, AndroidVideoUploadService::class.java).apply {
            action = ACTION_CANCEL_UPLOAD
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        currentJob?.cancel()
        isUploading = false
    }
}