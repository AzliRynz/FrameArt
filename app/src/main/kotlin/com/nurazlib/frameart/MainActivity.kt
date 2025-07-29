package com.nurazlib.frameart

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider

class MainActivity : AppCompatActivity(), LayerAdapter.OnLayerInteractionListener {

    private lateinit var drawingView: DrawingView
    private lateinit var layerAdapter: LayerAdapter
    private lateinit var layerList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi Views
        drawingView = findViewById(R.id.drawing_view)
        layerList = findViewById(R.id.layer_list)

        // Setup tombol kontrol
        setupControlButtons()

        // Setup RecyclerView untuk Layers
        setupLayerList()
    }

    private fun setupControlButtons() {
        val colorButton: ImageButton = findViewById(R.id.btn_color_picker)
        val strokeButton: ImageButton = findViewById(R.id.btn_stroke_width)
        val eraserButton: ImageButton = findViewById(R.id.btn_eraser)
        val addLayerButton: ImageButton = findViewById(R.id.btn_add_layer)
        val clearCanvasButton: ImageButton = findViewById(R.id.btn_clear_canvas)

        // TODO: Implement Color Picker Dialog
        colorButton.setOnClickListener {
            // Placeholder: set warna merah
            drawingView.setBrushColor(Color.RED)
        }

        strokeButton.setOnClickListener {
            showStrokeWidthDialog()
        }
        
        eraserButton.setOnClickListener {
            drawingView.setEraserMode(true)
        }

        addLayerButton.setOnClickListener {
            drawingView.addNewLayer()
            updateLayerList()
        }

        clearCanvasButton.setOnClickListener {
            showClearCanvasConfirmation()
        }
    }

    private fun setupLayerList() {
        layerAdapter = LayerAdapter(drawingView.getLayers(), this)
        layerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        layerList.adapter = layerAdapter
        updateLayerList()
    }

    // Fungsi untuk refresh daftar layer di UI
    private fun updateLayerList() {
        // Ambil index layer aktif dari drawingView
        val activeLayerIndex = drawingView.getLayers().indexOfFirst { it === drawingView.getLayers().getOrNull(drawingView.getLayers().lastIndex) }
        layerAdapter.updateLayers(drawingView.getLayers(), activeLayerIndex)
    }

    // --- Implementasi OnLayerInteractionListener ---

    override fun onLayerSelected(position: Int) {
        drawingView.setActiveLayer(position)
        updateLayerList()
    }

    override fun onLayerVisibilityChanged(position: Int) {
        drawingView.toggleLayerVisibility(position)
        updateLayerList()
    }

    override fun onLayerDeleted(position: Int) {
        if (drawingView.getLayers().size <= 1) {
            // Jangan hapus layer terakhir
            return
        }
        drawingView.removeLayerAt(position)
        updateLayerList()
    }

    // --- Dialog Helper ---

    private fun showStrokeWidthDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_stroke_width, null)
        val slider: Slider = dialogView.findViewById(R.id.stroke_slider)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Set Stroke Width")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                drawingView.setStrokeWidth(slider.value)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showClearCanvasConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clear Canvas")
            .setMessage("Are you sure you want to clear everything? This cannot be undone.")
            .setPositiveButton("Clear") { _, _ ->
                drawingView.clearCanvas()
                updateLayerList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
