package com.example.proyectopgl.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopgl.R
import com.example.proyectopgl.session.UserSession
import com.example.proyectopgl.ui.view.ToolbarView
import java.util.Locale

class ProfileActivity : AppCompatActivity(), VoiceRecognitionListener {
    private val SPEECH_REQUEST_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Perfil de usuario"

        val profileImage = findViewById<ImageView>(R.id.imageView3)
        val usernameTV = findViewById<TextView>(R.id.usernameTV)
        val emailTV = findViewById<TextView>(R.id.emailTV)
        val updatePasswordButton = findViewById<TextView>(R.id.updatePasswordButton)

        usernameTV.text = "Nombre de usuario: ${UserSession.UserSession.currentUser?.username}"
        emailTV.text = "Email: ${UserSession.UserSession.currentUser?.email}"
        profileImage.setImageResource(R.mipmap.perro)

        updatePasswordButton.setOnClickListener {
            val intent = Intent(this, UpdatePasswordActivity::class.java)
            startActivity(intent)
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
}