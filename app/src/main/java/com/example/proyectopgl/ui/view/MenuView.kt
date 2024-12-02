package com.example.proyectopgl.ui.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import com.example.proyectopgl.R
import com.example.proyectopgl.ui.RecordingsActivity
class MenuView(context: Context, attrs: AttributeSet?) :
    androidx.constraintlayout.widget.ConstraintLayout(context, attrs) {

    private val openRecordingActivity: ImageButton
    private val openRecordingLibrary: ImageButton
    private val openBeatLibrary: ImageButton
    private val openSongLibrary: ImageButton

    init {
        // Inflar el layout
        LayoutInflater.from(context).inflate(R.layout.menu_bar, this, true)

        // Inicialización de botones
        openRecordingActivity = findViewById(R.id.openRecordingActivity)
        openRecordingLibrary = findViewById(R.id.openRecordingLibrary)
        openBeatLibrary = findViewById(R.id.openBeatLibrary)
        openSongLibrary = findViewById(R.id.openSongLibrary)

        val currentActivity = this::class.java.simpleName

        // Listener para botones
        openRecordingActivity.setOnClickListener {
            val targetActivity = RecordingsActivity::class.java.simpleName
            Log.d("Activities", "Target: $targetActivity"+" Current: $currentActivity")
            if (currentActivity == targetActivity) {
                Toast.makeText(context, context.getString(R.string.alreadyOpen), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, RecordingsActivity::class.java)
                context.startActivity(intent)
            }
        }
        openRecordingActivity.setOnLongClickListener {
            Toast.makeText(context, context.getString(R.string.openRecordingActivity), Toast.LENGTH_SHORT).show()
            true
        }
        openRecordingLibrary.setOnClickListener {
            // Lógica aquí
        }
        openBeatLibrary.setOnClickListener {
            // Lógica aquí
        }
        openSongLibrary.setOnClickListener {
            // Lógica aquí
        }
    }
}
