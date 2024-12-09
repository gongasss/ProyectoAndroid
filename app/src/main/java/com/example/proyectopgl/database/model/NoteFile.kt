package com.example.proyectopgl.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_files")
data class NoteFile(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String,
    val noteContent: String,
    val date: String,
    val isFavorite: Boolean
) {

}