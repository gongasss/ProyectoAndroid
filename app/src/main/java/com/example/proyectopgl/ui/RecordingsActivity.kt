package com.example.proyectopgl.ui

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.adapter.AudioAdapter

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

        val recordings = listOf(
            RecordingFile(
                id = 1,
                title = "Recording 0001 - 01-01-2024",
                beat = "Beat",
                length = 19,
                date = "2023-05-01",
                folder = "",
                additionalInfo = "1poco raro pero ta bien",
                fileSize = "1.0 MB",
                filePath = ""
            ),
            RecordingFile(
                id = 2,
                title = "Recording 0002 - 01-01-2024",
                beat = "Beat",
                length = 19,
                date = "2023-05-01",
                folder = "",
                additionalInfo = "1poco raro pero ta bien",
                fileSize = "1.0 MB",
                filePath = ""
            ),
            RecordingFile(
                id = 3,
                title = "Recording 0003 - 01-01-2024",
                beat = "Beat",
                length = 19,
                date = "2023-05-01",
                folder = "",
                additionalInfo = "1poco raro pero ta bien",
                fileSize = "1.0 MB",
                filePath = ""
            ),
            RecordingFile(
                id = 4,
                title = "Recording 0004 - 01-01-2024",
                beat = "Beat",
                length = 19,
                date = "2023-05-01",
                folder = "",
                additionalInfo = "1poco raro pero ta bien",
                fileSize = "1.0 MB",
                filePath = ""
            ),
            RecordingFile(
                id = 5,
                title = "Recording 0005 - 01-01-2024",
                beat = "Beat",
                length = 19,
                date = "2023-05-01",
                folder = "",
                additionalInfo = "1poco raro pero ta bien",
                fileSize = "1.0 MB",
                filePath = ""
                ),
            )



        val recyclerView = findViewById<RecyclerView>(R.id.recordingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = AudioAdapter(recordings)

        val playPauseButton = findViewById<ImageButton>(R.id.playPauseButton)
        playPauseButton.setOnClickListener {
            alternatePlayPauseButton()
        }




    }
    fun alternatePlayPauseButton() {
        val playPauseButton = findViewById<ImageButton>(R.id.playPauseButton)
        if (playPauseButton.contentDescription == "Reproducir") {
            playPauseButton.setImageDrawable(getDrawable(R.drawable.baseline_pause_96))
            playPauseButton.contentDescription = "Pausar"
            // TODO: Implement play functionality

        } else {
            playPauseButton.setImageDrawable(getDrawable(R.drawable.baseline_play_arrow_96))
            playPauseButton.contentDescription = "Reproducir"
            // TODO: Implement pause functionality

        }
    }

    override fun onResume() {
        super.onResume()
        alternatePlayPauseButton()
    }
}