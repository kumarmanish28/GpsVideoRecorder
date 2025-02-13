package com.sisl.gpsvideorecorder.platform.videorecorder

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File

class AndroidVideoRecorder(private val context: Context) : VideoRecorder {
    private var videoCapture: VideoCapture<Recorder>? = null
    private var previewView: PreviewView? = null
    private var recording: Recording? = null
    private var isRecording = false

    init {
        previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView?.surfaceProvider
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                preview,
                videoCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    fun getPreviewView(): PreviewView? {
        return previewView
    }

    override fun startRecording() {
        if (isRecording) return

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "video_${System.currentTimeMillis()}.mp4"
        )

        val outputOptions = FileOutputOptions.Builder(file).build()

        recording = videoCapture?.output?.prepareRecording(context, outputOptions)
            ?.start(ContextCompat.getMainExecutor(context)) { event ->
                    if (event is VideoRecordEvent.Finalize) {

                        Log.d("CameraRecorder", "Recording saved: ${event.outputResults.outputUri}")
                        saveVideoToGallery(event.outputResults.outputUri) // Save properly
                    }
            }

        isRecording = true
    }

    override fun stopRecording() {
        recording?.stop()
        recording = null
        isRecording = false
    }

    override fun isRecording(): Boolean = isRecording

    private fun saveVideoToGallery(videoUri: Uri) {
        try {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, "video_${System.currentTimeMillis()}.mp4")
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            }

            resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?.let { uri ->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        resolver.openInputStream(videoUri)?.use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    Toast.makeText(context, "Video saved to gallery", Toast.LENGTH_SHORT).show()
                } ?: Log.e("CameraRecorder", "Failed to save video")
        } catch (ignore: Exception) {
        }
    }

}



actual fun getVideoRecorder(context: Any?): VideoRecorder {
    return AndroidVideoRecorder(context as Context)
}


@Composable
actual fun CameraPreview(recorder: VideoRecorder, context: Any?) {
    val androidRecorder = recorder as? AndroidVideoRecorder
    AndroidView(
        factory = { androidRecorder?.getPreviewView() ?: PreviewView(context as Context) },
        modifier = Modifier.fillMaxSize()
    )
}
