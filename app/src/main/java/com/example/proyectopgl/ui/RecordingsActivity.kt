package com.example.proyectopgl.ui

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.adapter.AudioAdapter
import com.example.proyectopgl.ui.view.ToolbarView
import kotlinx.coroutines.launch

class RecordingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recordings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Registro de grabaciones"

        setSupportActionBar(toolbar)

        supportActionBar?.title = "Grabadora"

        val database = AppDatabase.getInstance(this)
        var recordings: List<RecordingFile> = listOf()

        val recyclerView = findViewById<RecyclerView>(R.id.recordingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)

        lifecycleScope.launch {
            recordings = database.recordingDao().getAllRecordings()
            Log.d("RecordingsActivity", "Recordings: $recordings")
            recyclerView.adapter = AudioAdapter(recordings)
        }
    }
}