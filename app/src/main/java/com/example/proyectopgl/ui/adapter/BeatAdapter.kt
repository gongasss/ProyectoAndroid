package com.example.proyectopgl.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.database.model.BeatFile
import com.example.proyectopgl.ui.view.BeatItemView

class BeatAdapter(
    private var items: List<BeatFile>,
    private val listener: BeatItemView.OnBeatItemClickListener // Cambiado a la interfaz
) : RecyclerView.Adapter<BeatAdapter.BeatViewHolder>() {

    // ViewHolder
    inner class BeatViewHolder(itemView: BeatItemView) : RecyclerView.ViewHolder(itemView) {
        val beatItemView: BeatItemView = itemView

        fun bind(beat: BeatFile) {
            beatItemView.bind(beat) // Configurar la vista
            beatItemView.setOnBeatItemClickListener(listener) // Establecer el listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatViewHolder {
        val beatItemView = BeatItemView(parent.context)
        return BeatViewHolder(beatItemView)
    }

    override fun onBindViewHolder(holder: BeatViewHolder, position: Int) {
        val item = items[position]
        val beatView = holder.beatItemView // Aquí accedes al BeatItemView directamente

        beatView.setBeatDuration(item.duration.toString())
        beatView.setBeatFolder(item.folder)
        beatView.setBeatTitle(item.name)

        holder.bind(item) // Configurar la vista

    }

    override fun getItemCount(): Int = items.size

    // Método para actualizar la lista de beats
    fun updateItems(newItems: List<BeatFile>) {
        items = newItems
        notifyDataSetChanged() // Notifica que los datos han cambiado
    }
}