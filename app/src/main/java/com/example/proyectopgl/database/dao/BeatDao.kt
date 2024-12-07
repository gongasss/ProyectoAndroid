package com.example.proyectopgl.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.proyectopgl.database.model.BeatFile

@Dao
interface BeatDao {
    @Insert
    suspend fun insert(beat: BeatFile)

    @Query("SELECT * FROM beat_files WHERE id = :id")
    suspend fun getBeatById(id: Int): BeatFile?

    @Query("SELECT * FROM beat_files WHERE name = :name")
    suspend fun getBeatByName(name: String): BeatFile?

    @Query("SELECT * FROM beat_files ORDER BY duration DESC")
    suspend fun getAllBeats(): List<BeatFile>

    @Delete
    suspend fun delete(beat: BeatFile)
}