package com.example.proyectopgl.ui


import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.util.query
import com.example.proyectopgl.R
import com.example.proyectopgl.database.AppDatabase
import com.example.proyectopgl.database.model.BeatFile
import com.example.proyectopgl.ui.adapter.BeatAdapter
import com.example.proyectopgl.ui.view.BeatItemView
import com.example.proyectopgl.ui.view.PlayerView
import com.example.proyectopgl.ui.view.ToolbarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class BeatLibraryActivity : AppCompatActivity(), BeatItemView.OnBeatItemClickListener{
    private val database = AppDatabase.getInstance(this)
    val beatList = findViewById<RecyclerView>(R.id.beatList)

    // Adaptador de RecyclerView
    private lateinit var adapter: BeatAdapter
    private lateinit var playerView: PlayerView

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Log.d("BeatLibraryActivity", "Uri selected: $uri")
            handleAudioFile(it) // Procesa el archivo seleccionado
        } ?: run {
            // El usuario no seleccionó un archivo
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beat_library)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        playerView = findViewById(R.id.playerview)
        beatList.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador aquí
        var beats = listOf<BeatFile>()

        val toolbar: Toolbar = findViewById<ToolbarView>(R.id.toolbar)

        val activityTitle: TextView = toolbar.findViewById(R.id.activityTitle)

        activityTitle.text = "Registro de beats"


        checkAndRequestPermissions()



        loadBeatsFromDatabase()




        val addBeatButton = findViewById<ImageButton>(R.id.addBeatButton)

        addBeatButton.setOnClickListener {
            openFilePicker()
        }
    }
    override fun onPlayClick(beat: BeatFile) {
        playerView.setSong(beat.filePath)
        playerView.play()
    }
    override fun onDeleteClick(beat: BeatFile) {

    }
    private fun openFilePicker() {
        getContent.launch("audio/*")
    }
    private fun loadBeatsFromDatabase() {
        lifecycleScope.launch(Dispatchers.IO) {
            val beats = database.beatDao().getAllBeats()
            withContext(Dispatchers.Main) {
                adapter = BeatAdapter(beats, this@BeatLibraryActivity)
                beatList.adapter = adapter
            }
        }
    }

    private fun handleAudioFile(uri: Uri) {
        try {
            val fileName = getFileNameFromUri(uri, this)
            val filepath = fileName
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, getFileNameFromUri(uri, this)!!)
            file.outputStream().use { inputStream?.copyTo(it) }

            Log.d("BeatLibraryActivity", "File path: $filepath")
            val beat = filepath?.let {
                BeatFile(
                    name = fileName,
                    duration = getAudioLength(file),
                    folder = "",
                    filePath = filepath
                )
            }

            val database = AppDatabase.getInstance(this)
            val beatDao = database.beatDao()

            lifecycleScope.launch(Dispatchers.IO) {
                beatDao.insert(beat!!)
                withContext(Dispatchers.Main) {
                    Log.d("BeatLibraryActivity", "Beat inserted: $beat")
                    loadBeatsFromDatabase() // Actualiza la lista
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
            // Manejar errores aquí
            Log.d("BeatLibraryActivity", "Error: $e")
        }
    }

    fun getFileNameFromUri(uri: Uri, context: Context): String? {
        var fileName: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
    fun getAudioLength(file: File): Int {
        MediaMetadataRetriever().apply {
            setDataSource(file.absolutePath)
            val length = extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (length != null) {
                return length.toInt()
            }
        }
        return -1
    }
    private fun checkAndRequestPermissions() {
        // Verifica si el permiso ya fue otorgado
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no está otorgado, solicita el permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1001
            )
        } else {
            // El permiso ya está otorgado
            Toast.makeText(this, "Permiso ya otorgado", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
