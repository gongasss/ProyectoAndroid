package com.example.proyectopgl.ui.adapter

import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.view.RecordedItemView

class AudioAdapter(private val items: List<RecordingFile>) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    // ViewHolder que contiene una referencia a AudioItemView
    inner class AudioViewHolder(itemView: RecordedItemView) : RecyclerView.ViewHolder(itemView) {
        val recordedItemView: RecordedItemView = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        // Crear una nueva instancia de AudioItemView
        val recordedItemView = RecordedItemView(parent.context)
        return AudioViewHolder(recordedItemView)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = items[position]
        val audioView = holder.recordedItemView // Aquí accedes al AudioItemView directamente

        // Configurar los datos en la vista
        audioView.setAudioTitle(item.title)
        audioView.setBeatUsed(item.beat)
        audioView.setRecordingLength(item.length.toString())
        audioView.setRecordingDate(item.date)
        audioView.setParentFolder(item.folder)
        audioView.setAdditionalInfo(item.additionalInfo)

        // Sincronizar estado de expansión
        if (item.isExpanded) {
            audioView.expandDetails()
        } else {
            audioView.collapseDetails()
        }

        // Configurar listener para alternar expansión
        audioView.findViewById<ImageButton>(R.id.moreInfoButton).setOnClickListener {
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position) // Actualizar solo este ítem
        }
    }

    override fun getItemCount(): Int = items.size
}
