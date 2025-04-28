package com.sisl.gpsvideorecorder.data

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.contentValuesOf
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

// In commonMain
actual class VideoRecorder actual constructor() {
    private var cameraProvider: ProcessCameraProvider? = null
    private var recordingState = RecordingState.IDLE
    lateinit var context: Context
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var preview: Preview? = null
    private var isInitialized by mutableStateOf(false)


    actual fun initialize(onReady: () -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                isInitialized = true
                onReady()
            } catch (e: Exception) {
                Log.e("VideoRecorder", "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @RequiresPermission(
        allOf = [
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        ]
    )
    actual fun startRecording() {
        if (videoCapture == null) {
            Log.e("VideoRecorder", "VideoCapture is null - camera not ready")
            return
        }

        try {
            val outputFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                "video_${System.currentTimeMillis()}.mp4"
            ).apply { parentFile?.mkdirs() }

            val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
                .setContentValues(contentValuesOf(
                    MediaStore.Video.Media.DISPLAY_NAME to outputFile.name,
                    MediaStore.Video.Media.MIME_TYPE to "video/mp4"
                ))
                .build()

            recording = videoCapture?.output
                ?.prepareRecording(context, mediaStoreOutputOptions)
                ?.withAudioEnabled()
                ?.start(ContextCompat.getMainExecutor(context)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> recordingState = RecordingState.RECORDING
                        is VideoRecordEvent.Finalize -> {
                            recordingState = if (event.hasError()) {
                                Log.e("VideoRecorder", "Recording failed: ${event.error}")
                                RecordingState.STOPPED
                            } else {
                                Log.d("VideoRecorder", "Recording saved to ${outputFile.path}")
                                RecordingState.IDLE
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Recording start failed", e)
            recordingState = RecordingState.STOPPED
        }
    }
    actual fun stopRecording() {
        try {
            recording?.stop()
            recording = null
            recordingState = RecordingState.STOPPED
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Failed to stop recording", e)
            recordingState = RecordingState.STOPPED
        }
    }

    actual fun getRecordingState(): RecordingState = recordingState


    @Composable
    actual fun CameraPreview(modifier: Modifier) {
        if (!isInitialized) {
            Box(modifier = modifier.background(Color.Black))
            return
        }
        val lifecycleOwner = LocalLifecycleOwner.current
        val currentCameraProvider = cameraProvider ?: return

        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                }
            },
            modifier = modifier,
            update = { previewView ->

                try {
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    val newPreview = Preview.Builder().build()
                    preview = newPreview

                    val qualitySelector = QualitySelector.from(Quality.HD)
                    val recorder = Recorder.Builder()
                        .setQualitySelector(qualitySelector)
                        .build()
                    videoCapture = VideoCapture.withOutput(recorder)

                    currentCameraProvider.unbindAll()
                    currentCameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        newPreview,
                        videoCapture
                    )

                    newPreview.setSurfaceProvider(previewView.surfaceProvider)
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }

            })

    }

}

@Composable
actual fun rememberVideoRecorder(): VideoRecorder {
    val context = LocalContext.current
    return remember {
        VideoRecorder().apply {
            this.context = context
        }.also {
            it.initialize() // Initialize immediately
        }
    }
}