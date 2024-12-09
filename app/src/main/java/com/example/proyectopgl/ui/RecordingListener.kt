package com.example.proyectopgl.ui

import java.io.File

interface RecordingListener {
    fun onRecordingFinished(file: File)
    fun onStartRecording()
    fun onStopRecording()
}
