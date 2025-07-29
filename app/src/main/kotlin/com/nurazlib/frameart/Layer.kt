package com.nurazlib.frameart

import android.graphics.Paint
import android.graphics.Path

data class PaintedPath(val path: Path, val paint: Paint)

data class Layer(
    var name: String,
    val paths: MutableList<PaintedPath> = mutableListOf(),
    var isVisible: Boolean = true
)
