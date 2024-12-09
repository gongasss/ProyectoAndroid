package com.example.proyectopgl.ui.view

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyectopgl.R
import com.example.proyectopgl.database.model.BeatFile

class BeatItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val beatTitle: TextView
    private val beatDuration: TextView
    private val beatFolder: TextView
    private val deleteButton: ImageButton
    private val playPauseButton: ImageButton

    interface OnBeatItemClickListener {
        fun onPlayClick(beat: BeatFile)
        fun onDeleteClick(beat: BeatFile)
    }

    private var listener: OnBeatItemClickListener? = null
    private lateinit var currentBeat: BeatFile // Variable para almacenar el beat actual

    init {
        LayoutInflater.from(context).inflate(R.layout.beat_item, this, true)

        // Vincular los elementos del diseño
        beatTitle = findViewById(R.id.beatTitle)
        beatDuration = findViewById(R.id.beatDuration)
        beatFolder = findViewById(R.id.beatFolder)
        deleteButton = findViewById(R.id.deletebutton)
        playPauseButton = findViewById(R.id.playPauseButton)

        playPauseButton.setOnClickListener {
            listener?.onPlayClick(currentBeat) // Notificar al listener
        }
    }

    fun setOnBeatItemClickListener(listener: OnBeatItemClickListener) {
        this.listener = listener
    }

    // Configurar la vista con un objeto BeatFile
    fun bind(beat: BeatFile) {
        currentBeat = beat // Almacenar el beat actual
        beatTitle.text = beat.name
        beatDuration.text = context.getString(R.string.recordingLength, beat.duration.toString())
        beatFolder.text = beat.folder

        findViewById<ImageButton>(R.id.playButton).setOnClickListener {
            listener?.onPlayClick(beat) // Llama al listener
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("¿Estás seguro de que quieres eliminar este beat?")
                .setPositiveButton("Sí") { dialog, _ ->
                    listener?.onDeleteClick(beat) // Notificar al listener para eliminar
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
    fun setBeatTitle(title: String) {
        beatTitle.text = title
    }
    fun setBeatDuration(duration: String) {
        beatDuration.text = duration
    }
    fun setBeatFolder(folder: String) {
        beatFolder.text = folder
    }

}