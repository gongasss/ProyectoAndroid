package com.example.proyectopgl

import android.app.Application
import androidx.room.Room
import com.example.proyectopgl.database.AppDatabase

class App : Application() {
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)

    }
}
