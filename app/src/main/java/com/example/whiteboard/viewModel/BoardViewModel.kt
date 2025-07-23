package com.example.whiteboard.viewModel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.whiteboard.data.model.Shape
import com.example.whiteboard.data.model.Stroke
import com.example.whiteboard.data.model.TextBox
import com.example.whiteboard.data.model.WhiteBoardData
import kotlin.math.sqrt

/**
 *
 * @author Pradeep Prajapati
 * ViewModel class for
 *
 * */


class BoardViewModel : ViewModel() {

    /**
     * @author Pradeep Prajapati
     * live data handle
     * */
    private val _strokes = MutableLiveData<List<Stroke>>(emptyList())
    val strokes: LiveData<List<Stroke>> = _strokes

    private val _shapes = MutableLiveData<List<Shape>>(emptyList())
    val shapes: LiveData<List<Shape>> = _shapes

    private val _texts = MutableLiveData<List<TextBox>>(emptyList())
    val texts: LiveData<List<TextBox>> = _texts

    private val _currentColor = MutableLiveData("#000000")
    val currentColor: LiveData<String> = _currentColor

    private val _currentWidth = MutableLiveData(4f)
    val currentWidth: LiveData<Float> = _currentWidth

    var currentPoints = mutableListOf<Pair<Float, Float>>()
    private var eraserMode = false
    private var shapeMode: String? = null


    /**
     * @author Pradeep Prajapati
     * method for set different colors
     *
     **/
    fun setColor(color: String) {
        _currentColor.value = color
        eraserMode = false
        shapeMode = null
    }


    /**
     * @author Pradeep Prajapati
     * method for set different pointer size
     *
    **/
    fun setWidth(width: Float) {
        _currentWidth.value = width
    }

    /**
     * @author Pradeep Prajapati
     * Method for select Eraser
     * */
    fun toggleEraser() {
        eraserMode = !eraserMode
        shapeMode = null
    }

    /**
     * @author Pradeep Prajapati
     * Clear whiteboard
     * */
    fun clearAll() {
        _strokes.value = emptyList()
        _shapes.value = emptyList()
        _texts.value = emptyList()
        currentPoints = mutableListOf()
        shapeMode = null
        eraserMode = false
    }

    /**
     * @author Pradeep Prajapati
     * method for set shape mode like rectangle or circle
     * */
    fun setShapeMode(type: String) {
        shapeMode = type
        eraserMode = false
    }

    fun getShapeMode(): String? = shapeMode
    fun isShapeMode(): Boolean = shapeMode != null

    /**
     * Updates the shape preview while the user is dragging to draw a shape
     * */
    fun updateShapePreview(currentPoint: Pair<Float, Float>) {
        if (shapeMode != null && currentPoints.isNotEmpty()) {
            currentPoints = mutableListOf(currentPoints.first(), currentPoint)
        } else {
            addPoint(currentPoint)
        }
    }


    /**
     *
     *
     * @author Pradeep Prajapati
     * method for insert text every text in new line
     * */
    fun insertText(text: String, position: Pair<Float, Float>, size: Int = 24) {
        val currentLisy =  _texts.value.orEmpty()
        val newSecond =  position.second + currentLisy.size*size*1.5f
        val newText = TextBox(text, Pair(position.first, newSecond), _currentColor.value ?: "#000000", size)
        _texts.value = _texts.value.orEmpty() + newText
    }

    /**
     * @author Pradeep Prajapati
     * method for start stoke with selected color and stroke width
     * */
    fun startStroke(point: Pair<Float, Float>) {
        currentPoints = mutableListOf(point)

        val strokeColor = _currentColor.value ?: "#000000"
        val strokeWidth =  _currentWidth.value?: 4f
        val stroke = Stroke(mutableListOf(point), strokeColor, strokeWidth)
        _strokes.value = _strokes.value.orEmpty() + stroke
    }

    fun addPoint(point: Pair<Float, Float>) {
        if (eraserMode) {
            eraseAtPoint(point)
        } else {
            currentPoints.add(point)
        }
    }


    /**
     * @author Pradeep prajapati
     * erase stroke , shape
     * */
    private fun eraseAtPoint(point: Pair<Float, Float>) {

        val updatedStrokes = _strokes.value.orEmpty().mapNotNull { stroke ->
            val remainingPoints = stroke.points.filter {
                val dx = it.first - point.first
                val dy = it.second - point.second
                sqrt(dx * dx + dy * dy) > 40f
            }
            when {
                remainingPoints.isEmpty() -> null
                remainingPoints.size < stroke.points.size -> stroke.copy(points = remainingPoints)
                else -> stroke
            }
        }
        _strokes.value = updatedStrokes


        _shapes.value = _shapes.value.orEmpty().filterNot { pointInsideShape(point, it) }


        _texts.value = _texts.value.orEmpty().filterNot {
            val dx = point.first - it.position.first
            val dy = point.second - it.position.second
            sqrt(dx * dx + dy * dy) < 60f
        }
    }


    /**
     *if shape is active cretae shape
     *if shape is not selected and eraser mode is not active than create stroke
    * */
    fun endStroke() {

        if (shapeMode != null && currentPoints.size == 2) {
            val shape = Shape(
                type = shapeMode!!,
                topLeft = currentPoints[0],
                bottomRight = currentPoints[1],
                color = _currentColor.value ?: "#000000",
                strokeWidth = currentWidth.value!!
               
            )


            _shapes.value = _shapes.value.orEmpty() + shape


            currentPoints.clear()
            shapeMode = null
            return
        }

        if (!eraserMode && shapeMode == null && currentPoints.isNotEmpty()) {
            val stroke = Stroke(
                color = _currentColor.value ?: "#000000",
                width = _currentWidth.value ?: 4f,
                points = currentPoints.toList()
            )
            _strokes.value = _strokes.value.orEmpty() + stroke
        }

        currentPoints.clear()
    }

    /**
     * @author Pradeep Prajapti
     * method to store board as image
     * */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Uri? {
        val filename = "Whiteboard_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Whiteboard") // Saves to DCIM/Whiteboard folder
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }

        return uri
    }



    private fun pointInsideShape(point: Pair<Float, Float>, shape: Shape): Boolean {
        val x = point.first
        val y = point.second
        val left = minOf(shape.topLeft.first, shape.bottomRight.first)
        val right = maxOf(shape.topLeft.first, shape.bottomRight.first)
        val top = minOf(shape.topLeft.second, shape.bottomRight.second)
        val bottom = maxOf(shape.topLeft.second, shape.bottomRight.second)

        return x in left..right && y in top..bottom
    }


    /**
     * @author Pradeep Prajapati
     * save stroke , shape , text
     * */
    fun getWhiteboardData(): WhiteBoardData {
        return WhiteBoardData(
            stroke = _strokes.value.orEmpty(),
            shape = _shapes.value.orEmpty(),
            texts = _texts.value.orEmpty()
        )
    }

}