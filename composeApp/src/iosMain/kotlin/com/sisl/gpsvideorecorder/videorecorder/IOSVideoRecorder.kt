package com.sisl.gpsvideorecorder.videorecorder

import com.sisl.gpsvideorecorder.VideoRecorder
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureFileOutput
import platform.AVFoundation.AVCaptureFileOutputRecordingDelegateProtocol
import platform.AVFoundation.AVCaptureMovieFileOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVMediaTypeVideo
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSError
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject

class IOSVideoRecorder : VideoRecorder {
    private val captureSession = AVCaptureSession()
    private val movieOutput = AVCaptureMovieFileOutput()
    private var isRecording = false
    private var isRunning = false

    private val delegate = VideoRecorderDelegate() // Use a separate delegate

    init {
        setupCamera()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setupCamera() {
        val videoDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        val audioDevice = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeAudio)

        val videoInput = videoDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }
        val audioInput = audioDevice?.let { AVCaptureDeviceInput.deviceInputWithDevice(it, null) }

        if (videoInput != null) captureSession.addInput(videoInput)
        if (audioInput != null) captureSession.addInput(audioInput)

        captureSession.addOutput(movieOutput)
        captureSession.startRunning()

        isRunning = true  // Set flag when session starts
    }


    override fun startRecording() {
        if (isRecording || !isRunning) {  // Use isRunning to check session state
            println("Cannot start recording: Either already recording or session is not running.")
            return
        }

        val filePath = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true).first() as String
        val fileUrl = NSURL.fileURLWithPath("$filePath/video_${NSDate().timeIntervalSince1970}.mp4")

        movieOutput.startRecordingToOutputFileURL(fileUrl, delegate)
        isRecording = true
    }


    override fun stopRecording() {
        if (!isRecording || !isRunning || !movieOutput.isRecording()) {
            println("Attempted to stop recording, but it was not active.")
            return
        }

        movieOutput.stopRecording()
        isRecording = false
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