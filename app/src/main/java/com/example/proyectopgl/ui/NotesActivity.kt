package com.example.proyectopgl.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.NoteFile
import com.example.proyectopgl.ui.adapter.NoteAdapter
import com.example.proyectopgl.ui.view.ToolbarView
import kotlinx.coroutines.launch

class NotesActivity : AppCompatActivity() {


    val database = AppDatabase.getInstance(this)

    private lateinit var listView: ListView
    private lateinit var adapter: NoteAdapter
    private val notes: MutableList<NoteFile> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Registro de notas"

        val favCheckbox = findViewById<CheckBox>(R.id.favCheckbox)

        favCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {

                lifecycleScope.launch {
                    val favNotes = database.noteDao().getFavNotes()
                    notes.clear()
                    notes.addAll(favNotes)
                    adapter.notifyDataSetChanged()
                }
            } else {

                lifecycleScope.launch {
                    val allNotes = database.noteDao().getAllNotes()
                    notes.clear()
                    notes.addAll(allNotes)
                }
            }
        }
                listView = findViewById(R.id.listView)
                var newNoteButton = findViewById<Button>(R.id.addNewNoteButton)

                // Cargar datos desde la base de datos
                lifecycleScope.launch {
                    val notesDb = database.noteDao().getAllNotes()
                    Log.d("NotesActivity", "Notes: $notesDb")
                    notes.addAll(notesDb)
                }

                adapter = NoteAdapter(this, notes) { item ->
                    deleteItem(item)  // Método para eliminar
                }

                listView.adapter = adapter

                adapter.notifyDataSetChanged()

                newNoteButton.setOnClickListener {
                    Intent(this, NewNoteActivity::class.java).apply {
                        startActivity(this)
                    }
                }

            }
            private fun deleteItem(item: NoteFile) {
                val database = AppDatabase.getInstance(this)
                lifecycleScope.launch {
                    database.noteDao().deleteNoteById(item.id!!)
                    notes.remove(item)
                    adapter.notifyDataSetChanged()
                }

            }
        }