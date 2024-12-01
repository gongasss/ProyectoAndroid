package com.example.proyectopgl.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recording_files")
data class RecordingFile(
    @PrimaryKey val id: Int = 0,
    val title: String,
    val beat: String,
    val length: Int,
    val date: String,
    val folder: String,
    val additionalInfo: String,
    val fileSize: String,
    val filePath: String,
    var isExpanded: Boolean = false // Estado de expansión
)
