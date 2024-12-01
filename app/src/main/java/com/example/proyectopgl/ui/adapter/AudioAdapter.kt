package com.example.proyectopgl.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.view.AudioItemView

class AudioAdapter(private val items: List<RecordingFile>) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    // ViewHolder que contiene una referencia a AudioItemView
    inner class AudioViewHolder(itemView: AudioItemView) : RecyclerView.ViewHolder(itemView) {
        val audioItemView: AudioItemView = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        // Crear una nueva instancia de AudioItemView
        val audioItemView = AudioItemView(parent.context)
        return AudioViewHolder(audioItemView)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = items[position]
        val audioView = holder.audioItemView // Aquí accedes al AudioItemView directamente

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
