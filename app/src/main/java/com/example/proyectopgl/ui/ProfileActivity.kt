package com.example.proyectopgl.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectopgl.R
import com.example.proyectopgl.session.UserSession
import com.example.proyectopgl.ui.view.ToolbarView

class ProfileActivity : AppCompatActivity() {
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
}