package com.example.proyectopgl.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyectopgl.database.model.User
import com.example.proyectopgl.database.dao.UserDao

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
