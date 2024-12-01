package com.example.proyectopgl.ui.view
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import com.example.proyectopgl.R

class AudioItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val recordingTitle: TextView
    private val beatUsed: TextView
    private val recordLength: TextView
    private val playPauseButton: ImageButton
    private val detailsContainer: View
    private val moreInfoButton: ImageButton
    private val recordingDate: TextView
    private val parentFolder: TextView
    private val additionalInfo: TextView

    private var isExpanded = false // Estado inicial del contenedor de detalles

    init {
        // Inflar el diseño del componente
        LayoutInflater.from(context).inflate(R.layout.audio_item, this, true)

        // Vincular los elementos del diseño
        recordingTitle = findViewById(R.id.audioTitle)
        beatUsed = findViewById(R.id.beatUsed)
        recordLength = findViewById(R.id.recordDuration)
        playPauseButton = findViewById(R.id.playPauseButton)
        detailsContainer = findViewById(R.id.detailsContainer)
        moreInfoButton = findViewById(R.id.moreInfoButton)
        recordingDate = findViewById(R.id.recordingDate)
        parentFolder = findViewById(R.id.parentFolder)
        additionalInfo = findViewById(R.id.additionalInfo)
        
        


        // Configurar el listener para alternar expansión
        moreInfoButton.setOnClickListener {
            toggleDetails()
        }
    }

    // Método para alternar la visibilidad del contenedor de detalles
    private fun toggleDetails() {
        if (isExpanded) {
            collapseDetails()
        } else {
            expandDetails()
        }
        isExpanded = !isExpanded
    }

    fun expandDetails() {
        // Medir la altura deseada del contenedor
        detailsContainer.measure(
            MeasureSpec.makeMeasureSpec(detailsContainer.width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = detailsContainer.measuredHeight

        // Configurar la altura inicial en 0 para comenzar la animación
        detailsContainer.layoutParams.height = 0
        detailsContainer.visibility = View.VISIBLE

        // Crear la animación
        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            detailsContainer.layoutParams.height = value
            detailsContainer.requestLayout()
        }

        animator.duration = 300
        animator.start()
    }


    fun collapseDetails() {
        val initialHeight = detailsContainer.height

        // Crear la animación
        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            detailsContainer.layoutParams.height = value
            detailsContainer.requestLayout()
        }

        animator.duration = 300
        animator.start()

        // Al finalizar la animación, ocultar el contenedor
        animator.doOnEnd {
            detailsContainer.visibility = View.GONE
        }
    }


    // Métodos públicos para actualizar los datos
    fun setAudioTitle(title: String) {
        recordingTitle.text = title
    }

    fun setBeatUsed(beatName: String) {
        beatUsed.text = context.getString(R.string.recordings_beatUsed)+beatName
    }

    fun setRecordingLength(recordingLength: String) {
        recordLength.text = context.getString(R.string.recordingLength)+recordingLength+context.getString(R.string.secondsUnit)
    }

    fun setRecordingDate(date: String) {
        recordingDate.text = context.getString(R.string.recordingDate)+date
    }

    fun setParentFolder(folderName: String) {
        parentFolder.text = context.getString(R.string.recordingParentFolder)+folderName
    }
    fun setAdditionalInfo(additionalInfo: String) {
        this.additionalInfo.text = context.getString(R.string.recordingAdditionalInfo)+additionalInfo
    }
}
