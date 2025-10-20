package com.sisl.gpsvideorecorder.presentation.components.recorder

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream

// In commonMain
//actual class VideoRecorder actual constructor(
//    val onVideoRecorded: (VideoRecInfo) -> Unit,
//    val onSavingProgress: ((Float) -> Unit)?
//) {
//    private var cameraProvider: ProcessCameraProvider? = null
//    private var recordingState = RecordingState.STOPPED
//    lateinit var context: Context
//    private var videoCapture: VideoCapture<Recorder>? = null
//    private var recording: Recording? = null
//    private var preview: Preview? = null
//    private var isInitialized by mutableStateOf(false)
//    private var cameraSelector: CameraSelector? = null
//    private var videoFileName: String? = null
//    actual fun initialize(onReady: () -> Unit) {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//
//        cameraProviderFuture.addListener({
//            try {
//                cameraProvider = cameraProviderFuture.get()
//                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//                setupCameraUseCases()
//                isInitialized = true
//                onReady()
//            } catch (e: Exception) {
//                Log.e("VideoRecorder", "Camera initialization failed", e)
//            }
//        }, ContextCompat.getMainExecutor(context))
//    }
//
//    @RequiresPermission(
//        allOf = [
//            Manifest.permission.CAMERA,
//            Manifest.permission.RECORD_AUDIO
//        ]
//    )
//    actual fun startRecording() {
//        if (!isInitialized || videoCapture == null) {
//            return
//        }
//
//        try {
//            videoFileName = "android_video_${System.currentTimeMillis()}.mp4"
//            val videoFile = File(
//                context.getExternalFilesDir(Environment.DIRECTORY_DCIM),
//                videoFileName!!
//            )
//            val outputOptions = FileOutputOptions.Builder(videoFile).build()
//
//
//            recording = videoCapture?.output
//                ?.prepareRecording(context, outputOptions)
//                ?.withAudioEnabled()
//                ?.start(ContextCompat.getMainExecutor(context)) { event ->
//                    if (event is VideoRecordEvent.Finalize) {
//                        if (event.hasError()) {
//                            Log.e("CameraRecorder", "Recording error: ${event.error}")
//                        } else {
//                            Log.d(
//                                "CameraRecorder",
//                                "Recording saved: ${event.outputResults.outputUri}"
//                            )
//                            saveVideoToGallery(event.outputResults.outputUri)
//                        }
//                    }
//                }
//
//        } catch (e: Exception) {
//            recordingState = RecordingState.STOPPED
//        }
//    }
//
//    actual fun stopRecording() {
//        try {
//            recording?.stop()
//            recording = null
//            recordingState = RecordingState.STOPPED
//        } catch (e: Exception) {
//            Log.e("VideoRecorder", "Failed to stop recording", e)
//            recordingState = RecordingState.STOPPED
//        }
//    }
//
//    @Composable
//    actual fun CameraPreview(modifier: Modifier) {
//        if (!isInitialized) {
//            Box(modifier = modifier.background(Color.Black))
//            return
//        }
//        val lifecycleOwner = LocalLifecycleOwner.current
//        val currentCameraProvider = cameraProvider ?: return
//        val currentPreview = preview ?: return
//
//        AndroidView(
//            factory = { ctx ->
//                PreviewView(ctx).apply {
//                    scaleType = PreviewView.ScaleType.FILL_CENTER
//                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
//                }
//            },
//            modifier = modifier,
//            update = { previewView ->
//
//                try {
//                    currentCameraProvider.unbindAll()
//                    currentCameraProvider.bindToLifecycle(
//                        lifecycleOwner,
//                        cameraSelector ?: CameraSelector.DEFAULT_BACK_CAMERA,
//                        currentPreview,
//                        videoCapture
//                    )
//
//                    currentPreview.surfaceProvider = previewView.surfaceProvider
//                } catch (exc: Exception) {
//                    Log.e("CameraPreview", "Use case binding failed", exc)
//                }
//
//            })
//
//    }
//
//    private fun setupCameraUseCases() {
//        val qualitySelector = QualitySelector.from(Quality.HD)
//        val recorder = Recorder.Builder()
//            .setQualitySelector(qualitySelector)
//            .build()
//        videoCapture = VideoCapture.withOutput(recorder)
//        preview = Preview.Builder().build()
//    }
//
//    private fun saveVideoToGallery(videoUri: Uri) {
//        try {
//            val resolver = context.contentResolver
//            val contentValues = ContentValues().apply {
//                put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName!!)
//                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
//                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
//            }
//
//            resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
//                ?.let { uri ->
//                    resolver.openOutputStream(uri)?.use { outputStream ->
//                        resolver.openInputStream(videoUri)?.use { inputStream ->
//                            inputStream.copyTo(outputStream)
//                        }
//                    }
//                    Toast.makeText(context, "Video saved to gallery", Toast.LENGTH_SHORT).show()
//                } ?: Log.e("CameraRecorder", "Failed to save video")
//
//
//            val videoRecodedInfo = VideoRecInfo(
//                videoUri = videoUri.toString(),
//                videoName = videoFileName!!
//            )
//
//            onVideoRecorded(videoRecodedInfo)
//
//        } catch (ignore: Exception) {
//        }
//    }
//}


actual class VideoRecorder actual constructor(
    val onVideoRecorded: (VideoRecInfo) -> Unit,
    val onSavingProgress: ((Float) -> Unit)?
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var recordingState = RecordingState.STOPPED
    lateinit var context: Context
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var preview: Preview? = null
    private var isInitialized by mutableStateOf(false)
    private var cameraSelector: CameraSelector? = null
    private var videoFileName: String? = null
    private var videoFile: File? = null

    actual fun initialize(onReady: () -> Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                setupCameraUseCases()
                isInitialized = true
                onReady()
            } catch (e: Exception) {
                Log.e("VideoRecorder", "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @RequiresPermission(
        allOf = [
            Manifest.permission.CAMERA
            // REMOVED: Manifest.permission.RECORD_AUDIO
        ]
    )
    actual fun startRecording() {
        if (!isInitialized || videoCapture == null) {
            return
        }

        try {
            videoFileName = "android_video_${System.currentTimeMillis()}.mp4"
            videoFile = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                videoFileName!!
            )
            val outputOptions = FileOutputOptions.Builder(videoFile!!).build()

            recording = videoCapture?.output
                ?.prepareRecording(context, outputOptions)
                // REMOVED: .withAudioEnabled() - Audio recording disabled
                ?.start(ContextCompat.getMainExecutor(context)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            recordingState = RecordingState.RECORDING
                            Log.d("VideoRecorder", "Recording started without audio")
                        }
                        is VideoRecordEvent.Finalize -> {
                            recordingState = RecordingState.STOPPED
                            if (event.hasError()) {
                                Log.e("CameraRecorder", "Recording error: ${event.error}")
                            } else {
                                Log.d(
                                    "CameraRecorder",
                                    "Recording saved: ${event.outputResults.outputUri}"
                                )
                                // Start progress tracking when saving to gallery
                                saveVideoToGalleryWithProgress(event.outputResults.outputUri)
                            }
                        }
                        else -> {}
                    }
                }

        } catch (e: Exception) {
            recordingState = RecordingState.STOPPED
            Log.e("VideoRecorder", "Start recording failed", e)
        }
    }

    actual fun stopRecording() {
        try {
            recording?.stop()
            recording = null
            recordingState = RecordingState.STOPPED
            Log.d("VideoRecorder", "Recording stopped")
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Failed to stop recording", e)
            recordingState = RecordingState.STOPPED
        }
    }

    @Composable
    actual fun CameraPreview(modifier: Modifier) {
        if (!isInitialized) {
            Box(modifier = modifier.background(Color.Black))
            return
        }
        val lifecycleOwner = LocalLifecycleOwner.current
        val currentCameraProvider = cameraProvider ?: return
        val currentPreview = preview ?: return

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
                    currentCameraProvider.unbindAll()
                    currentCameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector ?: CameraSelector.DEFAULT_BACK_CAMERA,
                        currentPreview,
                        videoCapture
                    )

                    currentPreview.surfaceProvider = previewView.surfaceProvider
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }

            })

    }

    private fun setupCameraUseCases() {
        val qualitySelector = QualitySelector.from(Quality.HD)
        val recorder = Recorder.Builder()
            .setQualitySelector(qualitySelector)
            .build()
        videoCapture = VideoCapture.withOutput(recorder)
        preview = Preview.Builder().build()
    }

    private fun saveVideoToGalleryWithProgress(videoUri: Uri) {
        // Start progress at 0%
        onSavingProgress?.invoke(0f)

        // Use a coroutine to handle the file copy with progress
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resolver = context.contentResolver

                // Get the source file size for progress calculation
                val sourceFile = File(videoUri.path ?: "")
                val totalSize = sourceFile.length()

                val contentValues = ContentValues().apply {
                    put(MediaStore.Video.Media.DISPLAY_NAME, videoFileName!!)
                    put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                    put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                }

                resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?.let { galleryUri ->
                        resolver.openOutputStream(galleryUri)?.use { outputStream ->
                            resolver.openInputStream(videoUri)?.use { inputStream ->
                                // Copy with progress tracking
                                copyStreamWithProgress(inputStream, outputStream, totalSize)
                            }
                        }

                        // Complete progress
                        onSavingProgress?.invoke(100f)

                        // Notify completion
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Video saved to gallery", Toast.LENGTH_SHORT).show()

                            val videoRecodedInfo = VideoRecInfo(
                                videoUri = galleryUri.toString(),
                                videoName = videoFileName!!,
                                videoLocation = getVideoLocation(galleryUri)
                            )
                            onVideoRecorded(videoRecodedInfo)
                        }
                    } ?: run {
                    Log.e("CameraRecorder", "Failed to save video")
                    onSavingProgress?.invoke(0f) // Reset progress on failure
                }

            } catch (e: Exception) {
                Log.e("VideoRecorder", "Save to gallery failed", e)
                onSavingProgress?.invoke(0f) // Reset progress on failure
            }
        }
    }

    private suspend fun copyStreamWithProgress(
        inputStream: InputStream,
        outputStream: OutputStream,
        totalSize: Long
    ) {
        val buffer = ByteArray(8192)
        var bytesCopied = 0L
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
            bytesCopied += bytesRead

            // Calculate progress percentage
            val progress = (bytesCopied.toFloat() / totalSize.toFloat()) * 100f
            onSavingProgress?.invoke(progress.coerceIn(0f, 100f))

            // Small delay to make progress visible (optional)
            delay(10)
        }
    }

    private fun getVideoLocation(uri: Uri): String {
        return try {
            // Get the actual file path from URI
            val projection = arrayOf(MediaStore.Video.Media.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val pathColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    it.getString(pathColumn) ?: uri.toString()
                } else {
                    uri.toString()
                }
            } ?: uri.toString()
        } catch (e: Exception) {
            uri.toString()
        }
    }

    // Optional: Get current recording state
    fun getRecordingState(): RecordingState {
        return recordingState
    }
}
@Composable
actual fun rememberVideoRecorder(
    onVideoRecorded: (VideoRecInfo) -> Unit,
    onSavingProgress: ((Float) -> Unit)?
): VideoRecorder {
    val context = LocalContext.current
    val recorder = remember {
        VideoRecorder(
            onVideoRecorded = onVideoRecorded,
            onSavingProgress = onSavingProgress
        ).apply {
            this.context = context
        }
    }
    LaunchedEffect(Unit) {
        recorder.initialize()
    }

    return recorder
}