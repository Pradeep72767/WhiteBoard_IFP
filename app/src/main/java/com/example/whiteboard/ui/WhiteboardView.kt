package com.example.whiteboard.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.whiteboard.viewModel.BoardViewModel
import kotlin.math.max
import kotlin.math.min

class WhiteboardView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private lateinit var viewModel: BoardViewModel
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private var scaleFactor = 1.0f
    private val scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private val scaleMatrix = Matrix()


    fun setViewModel(vm: BoardViewModel) {
        viewModel = vm
        invalidate()
    }



    /**
     * @author Pradeep Prajapati
     * methos to savw board as image
     * */
    fun exportToBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.concat(scaleMatrix)


        /**
         * @author Pradeep prajapati
         * listing stroke size and draw stroke
         * */
        viewModel.strokes.value?.forEach { stroke ->
            paint.color = Color.parseColor(stroke.color)
            paint.strokeWidth = stroke.width
            val path = Path().apply {
                moveTo(stroke.points.first().first, stroke.points.first().second)
                stroke.points.drop(1).forEach { lineTo(it.first, it.second) }
            }
            Log.d("TAG", "onDraw: $path")
            canvas.drawPath(path, paint)
        }


        /**
         * drawing final shape
         * */
        viewModel.shapes.value?.forEach { shape ->
            paint.color = Color.parseColor(shape.color)
            paint.strokeWidth = shape.strokeWidth

            Log.d("TAG", "onDraw: $shape")
            when (shape.type) {
                "rectangle" -> canvas.drawRect(

                    shape.topLeft.first, shape.topLeft.second,
                    shape.bottomRight.first, shape.bottomRight.second, paint)
                "circle" -> {
                    val cx = (shape.topLeft.first + shape.bottomRight.first) / 2
                    val cy = (shape.topLeft.second + shape.bottomRight.second) / 2
                    val radius = minOf(
                        Math.abs(shape.bottomRight.first - shape.topLeft.first),
                        Math.abs(shape.bottomRight.second - shape.topLeft.second)) / 2
                    canvas.drawCircle(cx, cy, radius, paint)
                }
            }
        }


        /**
         * draw preaview while draging
         * */
        val shapeMode =  viewModel.getShapeMode()
        val  point =  viewModel.currentPoints
        if (shapeMode != null && point.size ==2){
            paint.color =  Color.parseColor(viewModel.currentColor.value)
            paint.strokeWidth = viewModel.currentWidth.value!!
            val (start, end) =  point
            when(shapeMode){
                "rectangle" -> canvas.drawRect(start.first, start.second, end.first, end.second, paint)
                "circle" ->{
                    val cx = (start.first + end.first) / 2
                    val cy = (start.second + end.second) / 2
                    val radius = minOf(
                        Math.abs(end.first - start.first),
                        Math.abs(end.second - start.second)
                    ) / 2
                    canvas.drawCircle(cx, cy, radius, paint)
                }
            }
        }


        /**
         *@author Pradeep Prajapati
         * listen selected color and place text
         * */
        viewModel.texts.value?.forEach { text ->
            textPaint.color = Color.parseColor(text.color)
            textPaint.textSize = text.size.toFloat()
            canvas.drawText(text.text, text.position.first, text.position.second, textPaint)
        }

        canvas.restore()
    }



    /**
     * @author Pradeep Prajapati
     * handle the pinch zoom and update the canvas
     * */
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor =  max(0.5f, min(scaleFactor, 3.0f))
            scaleMatrix.setScale(scaleFactor, scaleFactor)
            scaleFactor = scaleFactor.coerceIn(0.5f, 3.0f)
            invalidate()
            return true
        }
    }



    /**
     * @author Pradeep prajapati
     * handle touch event
     * */
    override fun onTouchEvent(event: MotionEvent): Boolean {

        scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress){
            val point =  event.x/scaleFactor to event.y/scaleFactor
            when(event.action){
                MotionEvent.ACTION_DOWN -> viewModel.startStroke(point)
                MotionEvent.ACTION_MOVE->{
                    if (viewModel.getShapeMode() != null) {
                        viewModel.updateShapePreview(point)
                    } else {
                        viewModel.addPoint(point)
                    }
                }
                MotionEvent.ACTION_UP -> viewModel.endStroke()
            }
        }
        invalidate()
        return true
    }
}