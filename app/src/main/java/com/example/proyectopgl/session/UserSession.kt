package com.example.proyectopgl.session

import com.example.proyectopgl.database.model.User

class UserSession {
    // UserSession.kt (un archivo separado o en tu clase de sesión)
    object UserSession {
        var currentUser: User? = null
    }

}