package com.sisl.gpsvideorecorder.platform.videorecorder


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureFileOutput
import platform.AVFoundation.AVCaptureFileOutputRecordingDelegateProtocol
import platform.AVFoundation.AVCaptureMovieFileOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

class IOSVideoRecorder : VideoRecorder {
    private val captureSession = AVCaptureSession()
    private val movieOutput = AVCaptureMovieFileOutput()
    private var isRecording = false
    private var isRunning = false
    private val sessionQueue = dispatch_queue_create("camera_session_queue", null)
    private val previewLayer: AVCaptureVideoPreviewLayer = AVCaptureVideoPreviewLayer(session = captureSession).apply {
        videoGravity = AVLayerVideoGravityResizeAspectFill
    }
    private val delegate = VideoRecorderDelegate()
    init {
        setupCamera()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setupCamera() {
        dispatch_async(sessionQueue) {
            // Configure devices on background queue
            val videoDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            val audioDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeAudio)

            try {
                captureSession.beginConfiguration()

                // Add video input
                videoDevice?.let { device ->
                    val videoInput = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
                    if (videoInput != null && captureSession.canAddInput(videoInput)) {
                        captureSession.addInput(videoInput)
                    }
                }

                // Add audio input
                audioDevice?.let { device ->
                    val audioInput = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
                    if (audioInput != null && captureSession.canAddInput(audioInput)) {
                        captureSession.addInput(audioInput)
                    }
                }

                // Add movie output
                if (captureSession.canAddOutput(movieOutput)) {
                    captureSession.addOutput(movieOutput)
                }

                captureSession.commitConfiguration()

                // Start session on background queue
                dispatch_async(sessionQueue) {
                    captureSession.startRunning()
                    isRunning = true
                }
            } catch (e: Exception) {
                println("Camera setup error: ${e.message}")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun getPreviewView(): UIView {
        val view = UIView().apply {
            layer.addSublayer(previewLayer)
            // Update frame on main thread
            dispatch_async(dispatch_get_main_queue()) {
                previewLayer.frame = bounds
            }
        }
        return view
    }

    override fun startRecording() {
        if (isRecording || !isRunning) return

        dispatch_async(sessionQueue) {
            val filePath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).first() as String
            val fileUrl = NSURL.fileURLWithPath("$filePath/video_${NSDate().timeIntervalSince1970}.mp4")

            movieOutput.startRecordingToOutputFileURL(fileUrl, delegate)
            isRecording = true
        }
    }

    override fun stopRecording() {
        if (!isRecording || !isRunning || !movieOutput.isRecording()) return

        dispatch_async(sessionQueue) {
            movieOutput.stopRecording()
            isRecording = false
        }
    }

    override fun isRecording(): Boolean = isRecording
}
/**
 * Delegate for AVCaptureFileOutputRecordingDelegateProtocol
 */
class VideoRecorderDelegate : NSObject(), AVCaptureFileOutputRecordingDelegateProtocol {
    override fun captureOutput(
        output: AVCaptureFileOutput,
        didFinishRecordingToOutputFileAtURL: NSURL,
        fromConnections: List<*>,
        error: NSError?
    ) {
        println("Recording finished: ${didFinishRecordingToOutputFileAtURL.absoluteString}")
    }
}

actual fun getVideoRecorder(context: Any?): VideoRecorder {
    return IOSVideoRecorder()
}

@Composable
actual fun CameraPreview(recorder: VideoRecorder, context: Any?) {
    val iosRecorder = recorder as? IOSVideoRecorder

    if (iosRecorder == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "No Preview available")
        }
    }

    UIKitView(
        factory = {
            iosRecorder?.getPreviewView() ?: UIView()
        },
        modifier = Modifier.fillMaxSize()
    )
}
