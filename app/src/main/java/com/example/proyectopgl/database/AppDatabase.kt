package com.example.proyectopgl.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectopgl.database.dao.BeatDao
import com.example.proyectopgl.database.dao.RecordingDao
import com.example.proyectopgl.database.model.User
import com.example.proyectopgl.database.dao.UserDao
import com.example.proyectopgl.database.model.BeatFile
import com.example.proyectopgl.database.model.RecordingFile

// se usa para crear la base de datos

@Database(entities = [User::class, RecordingFile::class, BeatFile::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recordingDao(): RecordingDao
    abstract fun beatDao(): BeatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
