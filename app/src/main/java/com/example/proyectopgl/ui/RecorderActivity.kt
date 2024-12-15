package com.example.proyectopgl.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.controller.AudioControlButton
import com.example.proyectopgl.ui.view.ToolbarView
import com.github.squti.androidwaverecorder.WaveRecorder
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.abs

class RecorderActivity : androidx.appcompat.app.AppCompatActivity(), VoiceRecognitionListener {

    private lateinit var audioButton: AudioControlButton

    private lateinit var gestureDetector: GestureDetector

    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var player: ExoPlayer
    private var waveRecorder: WaveRecorder? = null
    private var actualFilename: String = ""
    private var audioReady: Boolean = false
    private var playingAudio: Boolean = false

    private var currentFile: RecordingFile? = null

    private val SPEECH_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recorder)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gestureDetector = GestureDetector(this, GestureListener())

        val cbSupresionRuido = findViewById<CheckBox>(R.id.checkBox3)
        val recordingTitleET = findViewById<TextView>(R.id.recordingTitleET)
        val saveButton = findViewById<Button>(R.id.button3)
        val discardButton = findViewById<Button>(R.id.button4)

        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Grabadora"




        cbSupresionRuido.setOnCheckedChangeListener { _, isChecked -> Toast.makeText(this, "Supresión de ruido: $isChecked", Toast.LENGTH_SHORT).show() }

        saveButton.setOnClickListener {
            if(currentFile != null){
                val file = File(currentFile!!.filePath)
                file.copyTo(File(filesDir, currentFile!!.title), true)
                val database = AppDatabase.getInstance(this)
                lifecycleScope.launch {
                    database.recordingDao().insert(currentFile!!)
                    Toast.makeText(this@RecorderActivity, "Grabación guardada", Toast.LENGTH_SHORT).show()
                }
            }
        }
        discardButton.setOnClickListener {
            currentFile = null
            Toast.makeText(this, "Grabación descartada", Toast.LENGTH_SHORT).show()
        }

        val activityName = this::class.java.simpleName
        setSupportActionBar(toolbar)

        supportActionBar?.title = activityName

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1234
            )
        }

        val playButton = findViewById<ImageButton>(R.id.playButton)
        seekBar = findViewById(R.id.seekBar)
        player = ExoPlayer.Builder(this).build()
        playButton.setOnClickListener {
            if (audioReady) {
                seekBar.max = currentFile!!.length
                val handler = Handler()
                val updateSeekBar = object : Runnable {

                    override fun run() {
                        if(player.currentPosition >= player.duration){
                            playingAudio = false
                            playButton.setImageResource(R.drawable.baseline_play_arrow_16)
                            seekBar.progress = 0
                        }else{
                            seekBar.progress =
                                player.getCurrentPosition().toInt() // Método para obtener la posición actual
                            handler.postDelayed(this, 500) // Actualiza cada 500 ms
                        }

                    }
                }
                handler.post(updateSeekBar)

                if(!playingAudio){
                    player.play()
                    playButton.setImageResource(R.drawable.baseline_pause_16)
                }else{
                    player.stop()
                    playButton.setImageResource(R.drawable.baseline_play_arrow_16)
                }
            } else {
                Toast.makeText(this, "No se puede reproducir el audio", Toast.LENGTH_SHORT).show()
            }
        }

        audioButton = findViewById(R.id.audioButton)
        audioButton.setRecordingListener(object: RecordingListener{
            override fun onStartRecording() {
                val filename = "recording_${System.currentTimeMillis()}.mp3"
                val outputPath = cacheDir.absolutePath + "/" + filename // se guarda en cache
                actualFilename = filename

                Log.d("RecorderActivity", "File path: $outputPath")

                waveRecorder = WaveRecorder(outputPath)
                waveRecorder!!.noiseSuppressorActive = true
                if(!cbSupresionRuido.isChecked){
                    Log.d("RecorderActivity", "Supresion de ruido desactivada")
                    waveRecorder!!.noiseSuppressorActive = false
                }
                waveRecorder?.startRecording()
            }
            override fun onStopRecording() {
                waveRecorder?.stopRecording()
                waveRecorder = null

                val tempAudioFile = File(cacheDir, actualFilename)
                if (tempAudioFile.exists()) {
                    onRecordingFinished(tempAudioFile)
                    player.setMediaItem(MediaItem.fromUri(tempAudioFile.toUri()))
                    player.prepare()
                } else {
                    // Manejar caso de error
                    android.util.Log.e("RecorderActivity", "Archivo de audio no encontrado tras detener la grabación.")
                }
            }

            override fun onRecordingFinished(file: File) {
                audioReady = true
                val title: String =
                    if (recordingTitleET.text.toString().isEmpty()) {
                    file.name
                    } else {
                    recordingTitleET.text.toString()
                    }
                val recordingFile = RecordingFile(
                    title = title,
                    beat = "",
                    length = getAudioLength(file),
                    date = System.currentTimeMillis().toString(),
                    folder = "",
                    additionalInfo = "",
                    fileSize = "",
                    filePath = file.absolutePath
                )
                currentFile = recordingFile
            }
        })  // Asignar el listener


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
        val intent = Intent(this, NotesActivity::class.java)
        startActivity(intent)
    }

    private fun onSwipeLeft() {
        // Acción al deslizar hacia la izquierda
        val intent = Intent(this, RecordingsActivity::class.java)
        startActivity(intent)
    }

}