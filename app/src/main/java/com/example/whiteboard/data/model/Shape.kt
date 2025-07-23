package com.example.whiteboard.data.model

data class Shape(
    val type:String,
    val topLeft: Pair<Float, Float>,
    val bottomRight: Pair<Float, Float>,
    val color:String,
    val strokeWidth: Float

)
