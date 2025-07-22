package com.example.whiteboard.data.model

data class Stroke(
    val points :List<Pair<Float, Float>>,
    val color :String,
    val width :Float

)


