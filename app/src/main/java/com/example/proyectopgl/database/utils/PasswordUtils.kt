package com.example.proyectopgl.database.utils

import org.mindrot.jbcrypt.BCrypt

object PasswordUtils {

    // Hashear la contraseña
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    // Verificar la contraseña
    fun verifyPassword(inputPassword: String, storedHash: String): Boolean {
        return BCrypt.checkpw(inputPassword, storedHash)
    }
}
