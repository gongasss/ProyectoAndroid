package com.example.proyectopgl.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
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
import java.util.Locale
import kotlin.math.abs

class NotesActivity : AppCompatActivity(), VoiceRecognitionListener {


    val database = AppDatabase.getInstance(this)
    private lateinit var gestureDetector: GestureDetector

    private lateinit var listView: ListView
    private lateinit var adapter: NoteAdapter
    private val notes: MutableList<NoteFile> = mutableListOf()

    private val SPEECH_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gestureDetector = GestureDetector(this, GestureListener())


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

    /**
     * Método para iniciar el reconocimiento de voz
     */
    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora para transcribir tu voz")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible",
                Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * Método que recibe el resultado del reconocimiento de voz
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Verificamos si el resultado pertenece a nuestra solicitud de reconocimiento de voz
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            // Obtenemos una lista de posibles transcripciones del audio
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            // Si la lista no es nula, mostramos el primer resultado en el TextView
            result?.let {
                val spokenText = it[0].lowercase()
                when {

                    spokenText.contains(getString(R.string.wrd_recorder)) ||
                            spokenText.contains(getString(R.string.wrd_record)) ||
                            spokenText.contains(getString(R.string.wrd_recording)) -> {

                        Log.d("VoiceCommand", "Pantalla: Grabadora")
                        val targetActivity = RecorderActivity::class.java.simpleName
                        val intent = Intent(this, RecorderActivity::class.java)
                        startActivity(intent)
                    }


                    spokenText.contains(getString(R.string.wrd_recordings)) ||
                            spokenText.contains(getString(R.string.wrd_voiceNote)) -> {

                        Log.d("VoiceCommand", "Pantalla: Grabaciones")
                        val targetActivity = RecordingsActivity::class.java.simpleName
                        val intent = Intent(this, RecordingsActivity::class.java)
                        startActivity(intent)
                    }

                    spokenText.contains(getString(R.string.wrd_instrumentals)) ||
                            spokenText.contains(getString(R.string.wrd_instrumentalLibrary)) ||
                            spokenText.contains(getString(R.string.wrd_beatLibrary)) ||
                            spokenText.contains(getString(R.string.wrd_beats)) ||
                            spokenText.contains(getString(R.string.wrd_bits)) -> {

                        Log.d("VoiceCommand", "Pantalla: Beats")
                        val targetActivity = BeatLibraryActivity::class.java.simpleName
                        val intent = Intent(this, BeatLibraryActivity::class.java)
                        startActivity(intent)
                    }


                    spokenText.contains(getString(R.string.wrd_notes)) ||
                            spokenText.contains(getString(R.string.wrd_noteLibrary)) ||
                            spokenText.contains(getString(R.string.wrd_write)) ||
                            spokenText.contains(getString(R.string.wrd_text)) -> {

                        Log.d("VoiceCommand", "Pantalla: Notas")
                        val targetActivity = NotesActivity::class.java.simpleName
                        val intent = Intent(this, NotesActivity::class.java)
                        startActivity(intent)
                    }


                    else -> {
                        Log.d("VoiceCommand", "No se reconoció ninguna palabra clave válida")
                    }
                }
            }
        }
    }

    override fun startVoiceRecognition() {
        startSpeechToText()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Pasar el evento al detector de gestos
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100    // Distancia mínima del deslizamiento
        private val SWIPE_VELOCITY_THRESHOLD = 100  // Velocidad mínima del deslizamiento

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            return if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) { // movimiento hacia la derecha
                        onSwipeRight()
                    } else { // movimiento hacia la izquierda
                        onSwipeLeft()
                    }
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
    }

    private fun onSwipeRight() {
        // Acción al deslizar hacia la derecha
        val intent = Intent(this, BeatLibraryActivity::class.java)
        startActivity(intent)
    }

    private fun onSwipeLeft() {
        // Acción al deslizar hacia la izquierda
        val intent = Intent(this, RecorderActivity::class.java)
        startActivity(intent)
    }
}