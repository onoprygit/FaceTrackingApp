package com.onopry.facetrackingapp

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.face.Face
import java.lang.Exception
import java.util.concurrent.Executors

private const val TAG = "CameraManagerTAG"

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewFinder: PreviewView,
    private val overlay: FaceContourOverlay
) {

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var cameraProvider: ProcessCameraProvider
    private var cameraSelectorOpts = CameraSelector.LENS_FACING_FRONT
    private var faceTracker: FaceTracker? = null

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            faceProcessorInitialize()

            val faceAnalysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(overlay.width, overlay.height))
                .build()
                .also {
                    faceTracker?.let { faceTracker ->
                        it.setAnalyzer(cameraExecutor, faceTracker)
                    }
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraSelectorOpts)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    faceAnalysisUseCase
                )
            } catch (e: Exception) {
                Log.e(TAG, "Bind error", e)
            }

        }, context.mainExecutor)
    }

    private fun whenFrontCameraFaceBounds(face: Face) = Rect(
        overlay.width - face.boundingBox.left,
        face.boundingBox.top,
        overlay.width - face.boundingBox.right,
        face.boundingBox.bottom
    )

    private fun whenBackCameraFaceBounds(face: Face) = Rect(
        face.boundingBox.left,
        face.boundingBox.top,
        face.boundingBox.right,
        face.boundingBox.bottom
    )


    private fun faceProcessorInitialize() {
        faceTracker = FaceTracker { facesList ->
            val boundsList = ArrayList<Rect>()
            Log.d(TAG, "startCamera: counts of faces = ${facesList.size}")

            for (face in facesList) {
                var faceBounds = Rect()
                Log.d(TAG, "startCamera: face bounds = ${face.boundingBox}")

                if (isFrontCamera())
                    faceBounds.set(whenFrontCameraFaceBounds(face))
                else
                    faceBounds.set(whenBackCameraFaceBounds(face))

                boundsList.add(faceBounds)
            }
            overlay.faceBounds = boundsList
        }
    }

    fun removeCameraExecutor() {
        cameraExecutor.shutdown()
    }

    fun switchCamera() {
        cameraProvider.unbindAll()
        when (cameraSelectorOpts) {
            CameraSelector.LENS_FACING_FRONT -> cameraSelectorOpts = CameraSelector.LENS_FACING_BACK
            CameraSelector.LENS_FACING_BACK -> cameraSelectorOpts = CameraSelector.LENS_FACING_FRONT
        }
        startCamera()
    }

    private fun isFrontCamera() = cameraSelectorOpts == CameraSelector.LENS_FACING_FRONT

}