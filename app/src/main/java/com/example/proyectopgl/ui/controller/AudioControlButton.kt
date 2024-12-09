package com.example.proyectopgl.ui.controller

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.RecorderActivity
import com.example.proyectopgl.ui.RecordingListener
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File

class AudioControlButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    enum class AudioButtonState {
        RECORDING, STOPPED
    }

    private var currentState: AudioButtonState = AudioButtonState.STOPPED
    private val recordButton: ImageButton
    var actualFilename: String
    var listener: RecordingListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.record_control_button, this, true)
        recordButton = findViewById(R.id.recButton)
        recordButton.setImageResource(R.drawable.baseline_fiber_manual_record_96)
        actualFilename = ""

        recordButton.setOnClickListener {
            when (currentState) {
                AudioButtonState.STOPPED -> {
                    currentState = AudioButtonState.RECORDING
                    recordButton.setImageResource(R.drawable.baseline_stop_96)  // Icono de parar grabación
                    listener?.onStartRecording()  // Notificar a la actividad que inicie la grabación
                }
                AudioButtonState.RECORDING -> {
                    currentState = AudioButtonState.STOPPED
                    recordButton.setImageResource(R.drawable.baseline_fiber_manual_record_96)  // Icono de grabar
                    listener?.onStopRecording()  // Notificar a la actividad que detenga la grabación
                }
            }
        }
    }

    fun getState() = currentState
    fun setRecordingListener(listener: RecordingListener) {
        this.listener = listener
    }
}