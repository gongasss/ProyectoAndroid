package com.example.proyectopgl.ui


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.speech.RecognizerIntent
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.BeatFile
import com.example.proyectopgl.ui.adapter.BeatAdapter
import com.example.proyectopgl.ui.view.PlayerView
import com.example.proyectopgl.ui.view.ToolbarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.math.abs

interface VoiceRecognitionListener {
    fun startVoiceRecognition()
}

class BeatLibraryActivity : AppCompatActivity(), VoiceRecognitionListener {
    private val database = AppDatabase.getInstance(this)
    private lateinit var gestureDetector: GestureDetector


    // Adaptador de RecyclerView
    private lateinit var adapter: BeatAdapter
    private lateinit var playerView: PlayerView
    // Código de solicitud único para identificar el reconocimiento de voz cuando obtengamos el resultado
    private val SPEECH_REQUEST_CODE = 1

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.d("BeatLibraryActivity", "Uri selected: $uri")
            handleAudioFile(it) // Procesa el archivo seleccionado
        } ?: run {
            // El usuario no seleccionó un archivo
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beat_library)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gestureDetector = GestureDetector(this, GestureListener())

        var beatList: RecyclerView = findViewById(R.id.beatList)
        playerView = findViewById(R.id.playerview)
        beatList.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador aquí
        var beats = listOf<BeatFile>()

        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Registro de beats"


        checkAndRequestPermissions()



        lifecycleScope.launch(Dispatchers.IO) {
            val beats = database.beatDao().getAllBeats()
            withContext(Dispatchers.Main) {
                adapter = BeatAdapter(beats, this@BeatLibraryActivity)
                beatList.adapter = adapter
            }
        }




        val addBeatButton = findViewById<ImageButton>(R.id.addBeatButton)

        addBeatButton.setOnClickListener {
            openFilePicker()
        }
    }


    private fun openFilePicker() {
        getContent.launch("audio/*")
    }
    private fun loadBeatsFromDatabase() {

    }

    private fun handleAudioFile(uri: Uri) {
        try {
            val fileName = getFileNameFromUri(uri, this)
            val filepath = fileName
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, getFileNameFromUri(uri, this)!!)
            file.outputStream().use { inputStream?.copyTo(it) }

            Log.d("BeatLibraryActivity", "File path: $filepath")
            val beat = filepath?.let {
                BeatFile(
                    name = fileName,
                    duration = getAudioLength(file),
                    folder = "",
                    filePath = filepath
                )
            }

            val database = AppDatabase.getInstance(this)
            val beatDao = database.beatDao()

            lifecycleScope.launch(Dispatchers.IO) {
                beatDao.insert(beat!!)
                withContext(Dispatchers.Main) {
                    Log.d("BeatLibraryActivity", "Beat inserted: $beat")
                    loadBeatsFromDatabase() // Actualiza la lista
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar errores aquí
            Log.d("BeatLibraryActivity", "Error: $e")
        }
    }

    fun getFileNameFromUri(uri: Uri, context: Context): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    fun getAudioLength(file: File): Int {
        MediaMetadataRetriever().apply {
            setDataSource(file.absolutePath)
            val length = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (length != null) {
                return length.toInt()
            }
        }
        return -1
    }
    private fun checkAndRequestPermissions() {
        // Verifica si el permiso ya fue otorgado
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no está otorgado, solicita el permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1001
            )
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                // Permiso denegado

            }
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
                    if (diffX > 0) {
                        onSwipeRight()
                    } else {
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
        val intent = Intent(this, RecordingsActivity::class.java)
        startActivity(intent)
    }

    private fun onSwipeLeft() {
        // Acción al deslizar hacia la izquierda
        val intent = Intent(this, NotesActivity::class.java)
        startActivity(intent)
    }

}
