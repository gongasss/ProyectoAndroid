package com.example.proyectopgl.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectopgl.database.model.RecordingFile

@Dao
interface RecordingDao {
    @Insert
    suspend fun insert(recording: RecordingFile)

    @Query("SELECT * FROM recording_files WHERE id = :id")
    suspend fun getRecordingById(id: Int): RecordingFile?

    @Query("SELECT * FROM recording_files WHERE title = :title")
    suspend fun getRecordingByTitle(title: String): RecordingFile?

    @Query("SELECT * FROM recording_files ORDER BY date DESC")
    suspend fun getAllRecordings(): List<RecordingFile>

    @Delete
    suspend fun delete(recording: RecordingFile)
}