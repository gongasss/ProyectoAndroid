package com.example.proyectopgl.ui

import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopgl.R
import com.example.proyectopgl.database.model.RecordingFile
import com.example.proyectopgl.ui.adapter.AudioAdapter
import com.example.proyectopgl.ui.view.AudioItemView

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
                filePath = "C:/Users/pgona/Desktop/Recordings/Recording 0001 - 01-01-2024.mp3"
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
                filePath = "C:/Users/pgona/Desktop/Recordings/Recording 0002 - 01-01-2024.mp3"
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
                filePath = "C:/Users/pgona/Desktop/Recordings/Recording 0003 - 01-01-2024.mp3"
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
                filePath = "C:/Users/pgona/Desktop/Recordings/Recording 0004 - 01-01-2024.mp3"
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
                filePath = "C:/Users/pgona/Desktop/Recordings/Recording 0005 - 01-01-2024.mp3"
                ),
            )

        val recyclerView = findViewById<RecyclerView>(R.id.recordingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = AudioAdapter(recordings)



    }
}