package com.sisl.gpsvideorecorder.videorecorder

import android.content.Context
import android.os.Environment
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import com.sisl.gpsvideorecorder.VideoRecorder
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AndroidVideoRecorder(private val context: Context) : VideoRecorder {
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var isRecording = false
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    init {
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

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                videoCapture
            )
        }, ContextCompat.getMainExecutor(context))
    }

    override fun startRecording() {
        if (isRecording) return

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
            "video_${System.currentTimeMillis()}.mp4"
        )

        val outputOptions = FileOutputOptions.Builder(file).build()

        recording = videoCapture?.output?.prepareRecording(context, outputOptions)
            ?.start(ContextCompat.getMainExecutor(context)) {}

        isRecording = true
    }

    override fun stopRecording() {
        recording?.stop()
        recording = null
        isRecording = false
    }

    override fun isRecording(): Boolean = isRecording
}
