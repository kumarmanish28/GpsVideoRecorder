// In iosMain
package com.sisl.gpsvideorecorder.presentation.components.recorder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureFileOutput
import platform.AVFoundation.AVCaptureFileOutputRecordingDelegateProtocol
import platform.AVFoundation.AVCaptureMovieFileOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoOrientation
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeLeft
import platform.AVFoundation.AVCaptureVideoOrientationLandscapeRight
import platform.AVFoundation.AVCaptureVideoOrientationPortrait
import platform.AVFoundation.AVCaptureVideoOrientationPortraitUpsideDown
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.Foundation.NSDate
import platform.Foundation.NSError
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.Foundation.timeIntervalSince1970
import platform.QuartzCore.kCAGravityResizeAspectFill
import platform.UIKit.UIColor
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIDeviceOrientationDidChangeNotification
import platform.UIKit.UIView
import platform.darwin.NSObject

actual class VideoRecorder actual constructor(
    val onVideoRecorded: (VideoRecInfo) -> Unit,
    val onSavingProgress: ((Float) -> Unit)?
) {
    private val captureSession = AVCaptureSession().apply {
        usesApplicationAudioSession = true
    }
    private val videoOutput = AVCaptureMovieFileOutput()
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private var recordingState = RecordingState.STOPPED
    private var isInitialized = false
    private var videoFileName: String? = null


    actual fun initialize(onReady: () -> Unit) {
        configureCaptureSession()
        isInitialized = true
        onReady()
    }

    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun CameraPreview(modifier: Modifier) {
        if (!isInitialized) return

        var viewRef by remember { mutableStateOf<UIView?>(null) }

        UIKitView(
            modifier = modifier,
            factory = {
                val view = UIView()

                // Set background color (correct Kotlin/Native syntax)
                view.setBackgroundColor(UIColor.blackColor())

                // Create and configure preview layer
                previewLayer = AVCaptureVideoPreviewLayer(session = captureSession).apply {
                    setVideoGravity(kCAGravityResizeAspectFill)
                    setFrame(view.bounds)
                }

                // Add preview layer (correct layer hierarchy)
                previewLayer?.let {
                    view.layer.addSublayer(it)
                }

                // Store reference to the view
                viewRef = view

                view
            },
            update = { view: UIView ->
                // Update layer frame and orientation when view changes
                previewLayer?.setFrame(view.bounds)
                previewLayer?.connection?.setVideoOrientation(currentVideoOrientation())
            }
        )

        DisposableEffect(Unit) {
            val observer = NSNotificationCenter.defaultCenter.addObserverForName(
                name = UIDeviceOrientationDidChangeNotification,
                `object` = null,
                queue = NSOperationQueue.mainQueue
            ) { _ ->
                viewRef?.let { view ->
                    UIView.animateWithDuration(0.3) {
                        previewLayer?.frame = view.bounds
                        previewLayer?.connection?.videoOrientation = currentVideoOrientation()
                    }
                }
            }

            onDispose {
                NSNotificationCenter.defaultCenter.removeObserver(observer)
            }
        }
    }

    actual fun startRecording() {
        if (!isInitialized) return

        videoFileName = "ios_video_${NSDate().timeIntervalSince1970}.mp4"
        val outputPath =
            NSHomeDirectory() + "/Documents/$videoFileName"
        videoOutput.startRecordingToOutputFileURL(
            outputFileURL = NSURL.fileURLWithPath(outputPath),
            recordingDelegate = object : NSObject(), AVCaptureFileOutputRecordingDelegateProtocol {
                override fun captureOutput(
                    output: AVCaptureFileOutput,
                    didFinishRecordingToOutputFileAtURL: NSURL,
                    fromConnections: List<*>,
                    error: NSError?
                ) {
                    recordingState = if (error != null) {
                        println("Recording failed: ${error?.localizedDescription}")
                        RecordingState.STOPPED
                    } else {
                        println("Recording saved to: ${didFinishRecordingToOutputFileAtURL.path}")
                        val videoRecInfo = VideoRecInfo(
                            videoUri = outputPath,
                            videoName = videoFileName
                        )
                        onVideoRecorded(videoRecInfo)
                        RecordingState.STOPPED
                    }
                }
            }
        )
        recordingState = RecordingState.RECORDING
    }

    actual fun stopRecording() {
        videoOutput.stopRecording()
        recordingState = RecordingState.STOPPED
    }


    @OptIn(ExperimentalForeignApi::class)
    private fun configureCaptureSession() {
        captureSession.beginConfiguration()

        try {
            // Video input
            val videoDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
                ?: throw RuntimeException("No video device available")

            val videoInput = AVCaptureDeviceInput.deviceInputWithDevice(videoDevice, null)
                ?: throw RuntimeException("Could not create video input")

            if (captureSession.canAddInput(videoInput)) {
                captureSession.addInput(videoInput)
            }

            // Audio input
            val audioDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeAudio)
            val audioInput =
                audioDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }
            if (audioInput != null && captureSession.canAddInput(audioInput)) {
                captureSession.addInput(audioInput)
            }

            // Video output
            if (captureSession.canAddOutput(videoOutput)) {
                captureSession.addOutput(videoOutput)
            }

            captureSession.commitConfiguration()
            captureSession.startRunning()
        } catch (e: Exception) {
            captureSession.commitConfiguration()
            throw e
        }
    }

    private fun currentVideoOrientation(): AVCaptureVideoOrientation {
        return when (UIDevice.currentDevice.orientation) {
            UIDeviceOrientation.UIDeviceOrientationPortrait -> AVCaptureVideoOrientationPortrait
            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> AVCaptureVideoOrientationLandscapeLeft
            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> AVCaptureVideoOrientationLandscapeRight
            UIDeviceOrientation.UIDeviceOrientationFaceUp -> AVCaptureVideoOrientationPortraitUpsideDown
            else -> AVCaptureVideoOrientationPortrait
        }
    }
}

@Composable
actual fun rememberVideoRecorder(
    onVideoRecorded: (VideoRecInfo) -> Unit,
    onSavingProgress: ((Float) -> Unit)?
): VideoRecorder {
    return remember {
        VideoRecorder(
            onVideoRecorded = onVideoRecorded,
            onSavingProgress = onSavingProgress
        )
    }
}

