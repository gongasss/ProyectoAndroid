package com.example.proyectopgl.ui

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.App
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.UserRepository
import com.example.proyectopgl.database.model.User
import com.example.proyectopgl.database.utils.PasswordUtils
import com.example.proyectopgl.session.UserSession
import kotlinx.coroutines.launch
import java.util.Locale

class LoginActivity : AppCompatActivity(), VoiceRecognitionListener {

    var login_voiceButton: ImageButton? = null
    private val SPEECH_REQUEST_CODE = 1
    lateinit var etUsername: EditText
    lateinit var etPwd: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        etUsername = findViewById(R.id.etUsername)
        etPwd = findViewById(R.id.etPwd)
        login_voiceButton = findViewById(R.id.login_voiceButton)

        login_voiceButton?.setOnClickListener {
            startVoiceRecognition()
        }

        signUpButton.setOnClickListener {
            val database = AppDatabase.getInstance(this)
            val userDao = database.userDao()

            lifecycleScope.launch {
                var users: List<User> = listOf()
                users = userDao.getAllUsers()
                Log.d("Users", users[0].toString())
            }
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        loginButton.setOnClickListener {
            tryLogin(etUsername, etPwd)
        }

    }

    private fun tryLogin(etUsername: EditText, etPwd: EditText) {
        val database = AppDatabase.getInstance(this)
        val userDao = database.userDao()
        val userRepository = UserRepository(userDao)

        lifecycleScope.launch {
            if (!userRepository.userNameExists(etUsername.text.toString()) && !userRepository.emailExists(
                    etUsername.text.toString()
                )
            ) {
                etUsername.error = "El nombre de usuario/email no existe"
                return@launch
            }

            val user: User?
            if (etUsername.text.toString().contains("@")) {
                user = userRepository.getUserByEmail(etUsername.text.toString())
            } else {
                user = userRepository.getUserByUsername(etUsername.text.toString())
            }

            if (user != null && PasswordUtils.verifyPassword(
                    etPwd.text.toString(),
                    user.password
                )
            ) {
                Toast.makeText(
                    this@LoginActivity,
                    "Bienvenido ${user.username}",
                    Toast.LENGTH_SHORT
                ).show()
                UserSession.UserSession.currentUser = user
                startActivity(Intent(this@LoginActivity, RecordingsActivity::class.java))
            } else {
                etPwd.error = "La contraseña es incorrecta"
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

                    spokenText.contains(getString(R.string.wrd_signIn)) ||
                            spokenText.contains(getString(R.string.wrd_access)) ||
                            spokenText.contains(getString(R.string.wrd_login)) -> {

                        Log.d("VoiceCommand", "Pantalla: Grabadora")
                        tryLogin(etUsername, etPwd)
                    }

                    spokenText.contains(getString(R.string.wrd_create)) ||
                            spokenText.contains(getString(R.string.wrd_createAccount)) ||
                            spokenText.contains(getString(R.string.wrd_account)) ||
                            spokenText.contains(getString(R.string.wrd_register)) ||
                            spokenText.contains(getString(R.string.wrd_registry)) -> {

                        Log.d("VoiceCommand", "Pantalla: Beats")
                        val targetActivity = BeatLibraryActivity::class.java.simpleName
                        val intent = Intent(this, SignUpActivity::class.java)
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