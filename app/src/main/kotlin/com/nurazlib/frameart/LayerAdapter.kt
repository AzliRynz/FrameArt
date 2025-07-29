package com.nurazlib.frameart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LayerAdapter(
    private var layers: List<Layer>,
    private val listener: OnLayerInteractionListener
) : RecyclerView.Adapter<LayerAdapter.LayerViewHolder>() {

    private var selectedPosition = 0

    // Interface untuk komunikasi dengan Activity
    interface OnLayerInteractionListener {
        fun onLayerSelected(position: Int)
        fun onLayerVisibilityChanged(position: Int)
        fun onLayerDeleted(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layer_item, parent, false)
        return LayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: LayerViewHolder, position: Int) {
        val layer = layers[position]
        holder.bind(layer, position)
    }

    override fun getItemCount(): Int = layers.size

    // Fungsi untuk memperbarui data di adapter
    @SuppressLint("NotifyDataSetChanged")
    fun updateLayers(newLayers: List<Layer>, activeLayerIndex: Int) {
        this.layers = newLayers
        this.selectedPosition = activeLayerIndex
        notifyDataSetChanged() // Untuk simplicity, kita pakai ini. Bisa diganti dengan DiffUtil.
    }

    inner class LayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layerName: TextView = itemView.findViewById(R.id.layer_name)
        private val visibilityToggle: ImageView = itemView.findViewById(R.id.visibility_toggle)
        private val deleteButton: ImageView = itemView.findViewById(R.id.delete_layer)

        fun bind(layer: Layer, position: Int) {
            layerName.text = layer.name

            // Atur highlight untuk layer yang aktif
            itemView.setBackgroundColor(
                if (position == selectedPosition) {
                    ContextCompat.getColor(itemView.context, R.color.selected_layer_background)
                } else {
                    ContextCompat.getColor(itemView.context, android.R.color.transparent)
                }
            )

            // Atur ikon visibilitas
            val visibilityIcon = if (layer.isVisible) R.drawable.ic_visibility_on else R.drawable.ic_visibility_off
            visibilityToggle.setImageResource(visibilityIcon)

            // Atur listener
            itemView.setOnClickListener {
                listener.onLayerSelected(position)
            }
            visibilityToggle.setOnClickListener {
                listener.onLayerVisibilityChanged(position)
            }
            deleteButton.setOnClickListener {
                listener.onLayerDeleted(position)
            }
        }
    }
}
