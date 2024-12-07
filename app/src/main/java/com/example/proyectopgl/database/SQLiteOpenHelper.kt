package com.example.proyectopgl.database

import android.database.sqlite.SQLiteDatabase
import android.content.Context

// Aquí va la clase SQLiteOpenHelper base, que no está extendida por ningún otro helper
class SQLiteOpenHelper(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : android.database.sqlite.SQLiteOpenHelper(context, name, factory, version) {

    // Este método es llamado cuando la base de datos se crea por primera vez.
    override fun onCreate(db: SQLiteDatabase) {

        val createUsersTableQuery = """
            CREATE TABLE Users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL,
            email TEXT NOT NULL,
            password TEXT NOT NULL
            );
        """
        db.execSQL(createUsersTableQuery)

    }

    // Este método es llamado cuando se actualiza la base de datos.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Aquí puedes manejar cambios entre versiones de la base de datos (como eliminar tablas, agregar columnas, etc.)
        val createRecordingsTableQuery = """
            CREATE TABLE Recordings (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            beat TEXT,
            duration INTEGER NOT NULL,
            date TEXT NOT NULL,
            folder TEXT NOT NULL,
            additionalInfo TEXT,
            fileSize TEXT NOT NULL,
            filePath TEXT NOT NULL
            );
        """
        db.execSQL(createRecordingsTableQuery)
        val createBeatsTableQuery = """
            CREATE TABLE Beats (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            duration INTEGER NOT NULL,
            filePath TEXT NOT NULL
            );
        """
        db.execSQL(createBeatsTableQuery)
    }

    // Si quieres hacer algo más como realizar consultas o cambios, puedes agregar otros métodos aquí.

}
