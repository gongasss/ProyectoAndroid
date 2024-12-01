package com.example.proyectopgl.validator

import android.widget.EditText
import com.example.proyectopgl.database.UserRepository

// Clase de utilidades para la validación
class Validator(private val userRepository: UserRepository) {


    suspend fun validateExistingAccount(etUsername: EditText, etEmail: EditText): Boolean {
        if (userRepository.userNameExists(etUsername.text.toString())) {
            etUsername.error = "El nombre de usuario ya está en uso"
            return false
        }
        if (userRepository.emailExists(etEmail.text.toString())) {
            etEmail.error = "El email ya está en uso"
            return false
        }
        return true
    }

}
