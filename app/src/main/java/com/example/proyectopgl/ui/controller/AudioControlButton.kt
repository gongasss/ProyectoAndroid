package com.example.proyectopgl.ui.controller

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.example.proyectopgl.R
import com.example.proyectopgl.ui.RecorderActivity
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File

class AudioControlButton(context: Context, attrs: AttributeSet?) : AppCompatImageButton(context, attrs) {

    enum class AudioButtonState {
        RECORDING, STOPPED
    }

    private var currentState: AudioButtonState = AudioButtonState.STOPPED
    private var waveRecorder: WaveRecorder? = null

    init {
        setOnClickListener {
            when (currentState) {
                AudioButtonState.STOPPED -> startRecording()  // Cambia el estado a 'Recording'
                AudioButtonState.RECORDING -> stopRecording() // Cambia el estado a 'Paused'
            }
        }
    }

    private fun startRecording() {
        currentState = AudioButtonState.RECORDING
        setImageResource(R.drawable.baseline_stop_96)  // Icono de grabar
        // Lógica de grabación...
        val recordingFile = File(context.filesDir, "recording_${System.currentTimeMillis()}.mp3")

        waveRecorder = WaveRecorder(recordingFile.absolutePath)

        waveRecorder!!.startRecording()
    }

    fun stopRecording() {
        currentState = AudioButtonState.STOPPED
        setImageResource(R.drawable.baseline_fiber_manual_record_96)  // Icono de grabar
        this.waveRecorder?.stopRecording()

        // Lógica de guardar el archivo...


    }

    fun getState() = currentState
}
