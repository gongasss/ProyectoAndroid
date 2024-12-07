package com.example.proyectopgl.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "beat_files")
data class BeatFile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val duration: Int,
    val filePath: String
) {

}