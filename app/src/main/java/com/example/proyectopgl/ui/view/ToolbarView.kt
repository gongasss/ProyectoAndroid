package com.example.proyectopgl.ui.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity
import com.example.proyectopgl.R
import com.example.proyectopgl.ui.ProfileActivity
import com.example.proyectopgl.ui.RecorderActivity

class ToolbarView(context: Context, attrs: AttributeSet?) : androidx.appcompat.widget.Toolbar(context, attrs) {
    init {
        inflate(context, R.layout.toolbar, this)

        val profileImageButton = findViewById<ImageButton>(R.id.profileImageButton)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val activityTitle = findViewById<TextView>(R.id.activityTitle)



        profileImageButton.setOnClickListener {
            // Lógica para abrir la actividad de perfil
            Toast.makeText(context, "Perfil", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }

    }
    fun setTitle(title: String) {
        val activityTitle = findViewById<TextView>(R.id.activityTitle)
        activityTitle.text = title
    }

}