package com.example.proyectopgl.ui

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.NoteFile
import com.example.proyectopgl.ui.view.ToolbarView
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NewNoteActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Crear nueva nota"

        val saveNoteButton = findViewById<Button>(R.id.saveNoteButton)
        val noteTitleET = findViewById<EditText>(R.id.noteTitleET)
        val noteContentET = findViewById<EditText>(R.id.noteContentET)

        saveNoteButton.setOnClickListener {
            val title = noteTitleET.text.toString()
            val content = noteContentET.text.toString()
            // Guardar la nota en la base de datos

            val database = AppDatabase.getInstance(this)
            val note = NoteFile(title = title, noteContent = content, date = LocalDateTime.now().toString(), isFavorite = false)
            lifecycleScope.launch {
                database.noteDao().insert(note)
            }
            Toast.makeText(this, "Nota guardada", Toast.LENGTH_SHORT).show()
            finish()
        }

    }
}