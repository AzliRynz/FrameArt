package com.nurazlib.frameart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    // Daftar semua layer
    private val layers = mutableListOf<Layer>()
    private var activeLayerIndex = 0

    // Properti untuk kuas saat ini
    private var currentBrushColor = Color.BLACK
    private var currentStrokeWidth = 10f
    private var isErasing = false

    // Path yang sedang digambar
    private var currentPath: Path? = null
    private var currentPaint: Paint? = null

    init {
        // Tambahkan layer awal saat view dibuat
        if (layers.isEmpty()) {
            addNewLayer()
        }
    }

    // Fungsi untuk mengganti warna kuas
    fun setBrushColor(color: Int) {
        isErasing = false
        currentBrushColor = color
    }

    // Fungsi untuk mengganti ukuran kuas
    fun setStrokeWidth(width: Float) {
        currentStrokeWidth = width
    }

    // Fungsi untuk mengaktifkan mode penghapus
    fun setEraserMode(isErasing: Boolean) {
        this.isErasing = isErasing
    }

    // Mengatur layer mana yang aktif untuk digambar
    fun setActiveLayer(index: Int) {
        if (index >= 0 && index < layers.size) {
            activeLayerIndex = index
        }
    }

    // Menambah layer baru
    fun addNewLayer() {
        val newLayerName = "Layer ${layers.size + 1}"
        layers.add(Layer(newLayerName))
        activeLayerIndex = layers.size - 1
        invalidate()
    }

    // Menghapus layer pada posisi tertentu
    fun removeLayerAt(index: Int) {
        if (index >= 0 && index < layers.size) {
            layers.removeAt(index)
            // Jika layer aktif yang dihapus, pindah ke layer terakhir atau layer 0
            if (activeLayerIndex >= layers.size) {
                activeLayerIndex = layers.size - 1
            }
            if (activeLayerIndex < 0 && layers.isNotEmpty()) {
                activeLayerIndex = 0
            }
            invalidate()
        }
    }

    // Mengganti visibilitas layer
    fun toggleLayerVisibility(index: Int) {
        if (index >= 0 && index < layers.size) {
            layers[index].isVisible = !layers[index].isVisible
            invalidate()
        }
    }
    
    // Mendapatkan daftar layer untuk di-pass ke adapter
    fun getLayers(): List<Layer> = layers

    // Membersihkan seluruh canvas dengan menghapus semua layer dan membuat satu layer baru
    fun clearCanvas() {
        layers.clear()
        addNewLayer()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pastikan ada layer yang bisa digambar
        if (activeLayerIndex < 0 || activeLayerIndex >= layers.size) return false

        val activeLayer = layers[activeLayerIndex]
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path().apply { moveTo(x, y) }
                currentPaint = Paint().apply {
                    isAntiAlias = true
                    color = if (isErasing) Color.TRANSPARENT else currentBrushColor
                    strokeWidth = currentStrokeWidth
                    style = Paint.Style.STROKE
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                    // Mode penghapus menggunakan SRC_OUT untuk "menghapus" ke transparan
                    if (isErasing) {
                        xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OUT)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                currentPath?.let { path ->
                    currentPaint?.let { paint ->
                        activeLayer.paths.add(PaintedPath(path, paint))
                    }
                }
                currentPath = null
                currentPaint = null
            }
            else -> return false
        }

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Simpan state canvas sebelum menggambar apapun
        canvas.save()
        // Iterasi dan gambar setiap layer yang terlihat
        layers.forEach { layer ->
            if (layer.isVisible) {
                layer.paths.forEach { paintedPath ->
                    canvas.drawPath(paintedPath.path, paintedPath.paint)
                }
            }
        }
        // Restore state canvas
        canvas.restore()
    }
}
