package com.onopry.facetrackingapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

typealias OnFacePositionChangeListener = (faces: List<Face>) -> Unit

private const val TAG = "FaceTrackerTAG"

class FaceTracker(
    private val facesPositionListener: OnFacePositionChangeListener
) : ImageAnalysis.Analyzer {

    private val facesDetectionOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .build()

    private val detector = FaceDetection.getClient(facesDetectionOpts)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        mediaImage?.let {
            detector.process(
                InputImage.fromMediaImage(
                    mediaImage, imageProxy.imageInfo.rotationDegrees
                )
            ).addOnSuccessListener { faces ->
                Log.d(TAG, "analyze success: count of faces ${faces.size}")
                facesPositionListener.invoke(faces)
                for (face in faces) {
                    Log.d(TAG, "analyze success: bounds of each face ${face.boundingBox}")
                }
                imageProxy.close()
            }.addOnFailureListener {
                Log.e(TAG, "analyze failure: ${it.message}")
                imageProxy.close()
            }

        }
    }
}