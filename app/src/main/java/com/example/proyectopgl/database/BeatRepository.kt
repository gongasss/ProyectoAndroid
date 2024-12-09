package com.example.proyectopgl.database

import com.example.proyectopgl.database.dao.BeatDao

class BeatRepository(private val beatDao: BeatDao) {
    suspend fun insertAndRetrieveBeat(beat: com.example.proyectopgl.database.model.BeatFile): com.example.proyectopgl.database.model.BeatFile? {
        beatDao.insert(beat)
        return beatDao.getBeatByName(beat.name)
    }
    suspend fun getBeatByName(name: String): com.example.proyectopgl.database.model.BeatFile? {
        return beatDao.getBeatByName(name)
    }
    suspend fun getAllBeats(): List<com.example.proyectopgl.database.model.BeatFile> {
        return beatDao.getAllBeats()
    }
    suspend fun deleteBeat(beat: com.example.proyectopgl.database.model.BeatFile) {
        beatDao.delete(beat)
    }
}