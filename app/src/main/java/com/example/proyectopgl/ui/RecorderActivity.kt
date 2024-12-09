package com.example.proyectopgl.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Handler
import android.util.Log
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

class RecorderActivity : androidx.appcompat.app.AppCompatActivity() {

    private lateinit var audioButton: AudioControlButton

    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var player: ExoPlayer
    private var waveRecorder: WaveRecorder? = null
    private var actualFilename: String = ""
    private var audioReady: Boolean = false
    private var playingAudio: Boolean = false

    private var currentFile: RecordingFile? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recorder)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

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

}