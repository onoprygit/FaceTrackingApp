package com.onopry.facetrackingapp

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val viewFinder: PreviewView
) {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private var cameraSelectorOpts = CameraSelector.LENS_FACING_FRONT

    fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraSelectorOpts)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                Log.e(CameraManager::class.java.simpleName, "Bind error", e)
            }

        }, context.mainExecutor)
    }

    private fun setUpExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun removeCameraExecutor() {
        cameraExecutor.shutdown()
    }

    fun switchCamera() {
        cameraProvider.unbindAll()
        when(cameraSelectorOpts) {
            CameraSelector.LENS_FACING_FRONT -> cameraSelectorOpts = CameraSelector.LENS_FACING_BACK
            CameraSelector.LENS_FACING_BACK -> cameraSelectorOpts = CameraSelector.LENS_FACING_FRONT
        }
        startCamera()
    }

}