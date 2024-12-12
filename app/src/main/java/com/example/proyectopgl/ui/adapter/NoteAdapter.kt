package com.example.proyectopgl.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.NoteFile

class NoteAdapter(
    private val context: Context,
    private val items: List<NoteFile>,
    private val onDeleteClick: (NoteFile) -> Unit
    ) : BaseAdapter() {
        override fun getCount(): Int {
            return items.size
        }

        override fun getItem(position: Int): NoteFile {
            return items[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)

        val itemText = view.findViewById<TextView>(R.id.item_text)
        val deleteButton = view.findViewById<ImageButton>(R.id.delete_button)
        val favButton = view.findViewById<ImageButton>(R.id.favButton)

        val item = getItem(position)
        itemText.text = item.title  // Ajusta según tu data class

        deleteButton.setOnClickListener {
            onDeleteClick(item)  // Notifica al callback que se pulsó el botón
        }
        favButton.setOnClickListener {
            val database = AppDatabase.getInstance(context)
            if(item.isFavorite){
                item.isFavorite = false
                favButton.setImageResource(R.drawable.baseline_favorite_border_24)

            }else{
                item.isFavorite = true
                favButton.setImageResource(R.drawable.baseline_favorite_24)
            }
        }

        return view
    }
}