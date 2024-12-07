package com.example.proyectopgl.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.BeatFile
import kotlinx.coroutines.launch
import java.io.File

class BeatLibraryActivity : androidx.appcompat.app.AppCompatActivity() {
    val database = AppDatabase.getInstance(this)
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            handleAudioFile(it) // Procesa el archivo seleccionado
        } ?: run {
            // El usuario no seleccionó un archivo
        }
    }
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beat_library)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




        val beatList = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.beatList)

        var beats: List<BeatFile>

        lifecycleScope.launch {
            beats = database.beatDao().getAllBeats()
        }

        val adapter = BeatAdapter(beats)
        beatList.adapter = adapter

        val addBeatButton = findViewById<ImageButton>(R.id.addBeatButton)
        addBeatButton.setOnClickListener {
            // TODO: Implementar la función para agregar un nuevo beat
            openFilePicker()
        }

    }
    private fun openFilePicker() {
        getContent.launch("audio/*")
    }
    private fun handleAudioFile(uri: Uri) {
        try {
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor
            fileDescriptor?.let {
                // Ejemplo: reproducir el archivo con MediaPlayer
                val filepath = File(uri.path).absolutePath

            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar errores aquí
        }
    }
    private fun insertBeat(filepath: String) {
        val beat = BeatFile(
            id = 0,
            name = File(filepath).name,
            duration = 0,
            filePath = filepath
        )

        lifecycleScope.launch {
            val newBeat = database.beatDao().insert(beat)
            Log.d("BeatLibraryActivity", "New beat inserted: $newBeat") // TODO: Implement logic to add the new beat to the database
        }
    }

}