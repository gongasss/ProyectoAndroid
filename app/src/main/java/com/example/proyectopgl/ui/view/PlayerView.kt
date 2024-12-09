package com.example.proyectopgl.ui.view

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.proyectopgl.R


class PlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val horizontalScrollView: HorizontalScrollView
    private val textView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var scrollX = 0
    private val scrollSpeed = 2 // Ajusta la velocidad de desplazamiento
    private var player: ExoPlayer? = null

    init {
        // Inflar el layout
        LayoutInflater.from(context).inflate(R.layout.player_view, this, true)
        horizontalScrollView = findViewById(R.id.titleScrollView)
        textView = findViewById(R.id.titleTV)

        // Iniciar el desplazamiento
        startScrolling()

        // Inicializar el reproductor
        player = ExoPlayer.Builder(context).build()
    }

    private fun startScrolling() {
        handler.post(object : Runnable {
            override fun run() {
                // Desplazar el contenido
                scrollX += scrollSpeed
                horizontalScrollView.scrollTo(scrollX, 0)

                // Reiniciar el desplazamiento si ha llegado al final
                if (scrollX > textView.width) {
                    scrollX = 0
                }

                // Volver a ejecutar el Runnable después de un pequeño retraso
                handler.postDelayed(this, 30) // Ajusta el tiempo para cambiar la velocidad
            }
        })
    }

    fun setSong(uri: String) {
        // Crear un MediaItem a partir de la URI
        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    fun play() {
        player?.playWhenReady = true
    }

    fun pause() {
        player?.playWhenReady = false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Detener el handler cuando el componente se destruye
        handler.removeCallbacksAndMessages(null)
        player?.release() // Liberar el reproductor
    }
}