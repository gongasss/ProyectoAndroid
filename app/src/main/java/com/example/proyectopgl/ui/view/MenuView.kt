package com.example.proyectopgl.ui.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.proyectopgl.R
import com.example.proyectopgl.ui.BeatLibraryActivity
import com.example.proyectopgl.ui.NotesActivity
import com.example.proyectopgl.ui.RecorderActivity
import com.example.proyectopgl.ui.RecordingsActivity
import com.example.proyectopgl.ui.VoiceRecognitionListener
import java.util.Locale

class MenuView(context: Context, attrs: AttributeSet?) :
    androidx.constraintlayout.widget.ConstraintLayout(context, attrs) {

    private val openRecordingActivity: ImageButton
    private val openRecordingLibrary: ImageButton
    private val openBeatLibrary: ImageButton
    private val openSongLibrary: ImageButton
    private val startVoiceRecognitionButton: ImageButton
    private var voiceRecognitionListener: VoiceRecognitionListener? = null

    init {
        // Inflar el layout
        LayoutInflater.from(context).inflate(R.layout.menu_bar, this, true)

        // Inicialización de botones
        openRecordingActivity = findViewById(R.id.openRecordingActivity)
        openRecordingLibrary = findViewById(R.id.openRecordingLibrary)
        openBeatLibrary = findViewById(R.id.openBeatLibrary)
        openSongLibrary = findViewById(R.id.openSongLibrary)
        startVoiceRecognitionButton = findViewById(R.id.startVoiceRecognitionButton)

        // Verifica si el contexto implementa la interfaz
        if (context is VoiceRecognitionListener) {
            voiceRecognitionListener = context
        } else {
            throw RuntimeException("$context debe implementar VoiceRecognitionListener")
        }

        val currentActivity = this::class.java.simpleName

        startVoiceRecognitionButton.setOnClickListener {
            voiceRecognitionListener?.startVoiceRecognition()
        }

        // Listener para botones
        openRecordingActivity.setOnClickListener {
            val targetActivity = RecorderActivity::class.java.simpleName
            Log.d("Activities", "Target: $targetActivity"+" Current: $currentActivity")
            if (currentActivity == targetActivity) {
                Toast.makeText(context, context.getString(R.string.alreadyOpen), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, RecorderActivity::class.java)
                context.startActivity(intent)
            }
        }
        openRecordingActivity.setOnLongClickListener {
            Toast.makeText(context, context.getString(R.string.openRecordingActivity), Toast.LENGTH_SHORT).show()
            true
        }
        openRecordingLibrary.setOnClickListener {
            val targetActivity = RecordingsActivity::class.java.simpleName
            Log.d("Activities", "Target: $targetActivity"+" Current: $currentActivity")
            if (currentActivity == targetActivity) {
                Toast.makeText(context, context.getString(R.string.alreadyOpen), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, RecordingsActivity::class.java)
                context.startActivity(intent)
            }
        }
        openRecordingLibrary.setOnLongClickListener {
            Toast.makeText(context, context.getString(R.string.openRecordingLibraryActivity), Toast.LENGTH_SHORT).show()
            true
        }
        openBeatLibrary.setOnClickListener {
            // Lógica aquí
            val targetActivity = BeatLibraryActivity::class.java.simpleName
            Log.d("Activities", "Target: $targetActivity"+" Current: $currentActivity")
            if (currentActivity == targetActivity) {
                Toast.makeText(context, context.getString(R.string.alreadyOpen), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, BeatLibraryActivity::class.java)
                context.startActivity(intent)
            }
        }
        openBeatLibrary.setOnLongClickListener {
            Toast.makeText(context, context.getString(R.string.navigateToBeatsButton), Toast.LENGTH_SHORT).show()
            true
        }
        openSongLibrary.setOnClickListener {
            // Lógica aquí
            val targetActivity = NotesActivity::class.java.simpleName
            Log.d("Activities", "Target: $targetActivity"+" Current: $currentActivity")
            if (currentActivity == targetActivity) {
                Toast.makeText(context, context.getString(R.string.alreadyOpen), Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(context, NotesActivity::class.java)
                context.startActivity(intent)
            }
        }
        openSongLibrary.setOnLongClickListener {
            Toast.makeText(context, context.getString(R.string.navigateToNotes), Toast.LENGTH_SHORT).show()
            true
        }

    }


}
