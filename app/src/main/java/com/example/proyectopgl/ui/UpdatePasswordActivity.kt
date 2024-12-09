package com.example.proyectopgl.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.UserRepository
import com.example.proyectopgl.database.utils.PasswordUtils
import com.example.proyectopgl.session.UserSession
import kotlinx.coroutines.launch

class UpdatePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.title = R.string.update_password.toString()

        val currentPasswordET = findViewById<EditText>(R.id.currentPasswordET)
        val newPassword1 = findViewById<EditText>(R.id.newPassword1)
        val newPassword2 = findViewById<EditText>(R.id.newPassword2)

        val confirmUpdateButton = findViewById<Button>(R.id.button)

        confirmUpdateButton.setOnClickListener {
            val user = UserSession.UserSession.currentUser
            if (user != null) {
                if(PasswordUtils.verifyPassword(currentPasswordET.text.toString(), user.password)){
                    if(newPassword1.text.toString() == newPassword2.text.toString()){
                        val database = AppDatabase.getInstance(this)
                        val userDao = database.userDao()
                        val userRepository = UserRepository(userDao)
                        lifecycleScope.launch {
                            userRepository.updateUserPassword(user.id.toInt(), PasswordUtils.hashPassword(newPassword1.text.toString()))
                        }
                        finish()
                    }
                }

            }
        }

    }
}