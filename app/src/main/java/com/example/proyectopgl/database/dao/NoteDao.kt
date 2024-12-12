package com.example.proyectopgl.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectopgl.database.model.NoteFile

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: NoteFile)

    @Query("SELECT * FROM note_files WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteFile?

    @Query("SELECT * FROM note_files ORDER BY id DESC")
    suspend fun getAllNotes(): List<NoteFile>

    @Query("DELETE FROM note_files WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("DELETE FROM note_files")
    suspend fun deleteAllNotes()

    @Query("UPDATE note_files SET isFavorite = :isFavorite WHERE id = :id")
    fun updateNote(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM note_files WHERE isFavorite = 1")
    suspend fun getFavNotes(): List<NoteFile>

}