package com.example.proyectopgl.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.App
import com.example.proyectopgl.R
import com.example.proyectopgl.database.UserRepository
import com.example.proyectopgl.database.utils.PasswordUtils
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
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
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPwd = findViewById<EditText>(R.id.etPwd)

        signUpButton.setOnClickListener {
            startActivity(Intent(this, RecordingsActivity::class.java))
        }

        loginButton.setOnClickListener {
            val database = (application as App).database
            val userDao = database.userDao()
            val userRepository = UserRepository(userDao)

            lifecycleScope.launch {
                if(!userRepository.userNameExists(etUsername.text.toString())){
                    etUsername.error = "El nombre de usuario no existe"
                    return@launch
                }
                val user = userRepository.getUserByUsername(etUsername.text.toString())
                if(user != null && PasswordUtils.verifyPassword(etPwd.text.toString(), user.password)){
                    Toast.makeText(this@LoginActivity, "Bienvenido ${user.username}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, RecordingsActivity::class.java))
                }else{
                    etPwd.error = "La contraseña es incorrecta"
                }
            }

        }

    }
}