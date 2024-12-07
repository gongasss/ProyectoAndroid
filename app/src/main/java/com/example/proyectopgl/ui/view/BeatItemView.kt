package com.example.proyectopgl.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.proyectopgl.R

class BeatItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val beatTitle: TextView
    private val beatDuration: TextView
    private val beatFolder: TextView
    private val deleteButton: ImageButton
    init {
        LayoutInflater.from(context).inflate(R.layout.beat_item, this, true)

        // Vincular los elementos del diseño
        beatTitle = findViewById(R.id.beatTitle)
        beatDuration = findViewById(R.id.beatDuration)
        beatFolder = findViewById(R.id.beatFolder)
        deleteButton = findViewById(R.id.deletebutton)


    }
}