package com.onopry.facetrackingapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.camera.core.CameraSelector
import com.google.mlkit.vision.face.Face

class FaceContourOverlay(
    context: Context?,
    attr: AttributeSet?
) : View(context, attr) {

    var cameraSelector= CameraSelector.DEFAULT_FRONT_CAMERA
//    private val facesBounds = ArrayList<Rect>()
    var faceBounds = ArrayList<Rect>()
        set(bounds) {
            field.clear()
            field.addAll(bounds)
            this.postInvalidate()
        }

    private val paint = Paint().apply {
        strokeWidth = 2f * (context?.resources?.displayMetrics?.density ?: 400f)
        color = Color.GREEN
        style = Paint.Style.STROKE
    }

//    fun setFaceBounds(bounds: List<Rect>) {
//        facesBounds.clear()
//        facesBounds.addAll(bounds)
//        this.postInvalidate()
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        for (bound in faceBounds)
            canvas?.let { drawFace(it, bound) }
    }

    private fun drawFace(canvas: Canvas, bound: Rect) {
        canvas.drawRect(bound, paint)
    }

}