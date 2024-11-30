package com.example.proyectopgl

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
    }

    // Si quieres hacer algo más como realizar consultas o cambios, puedes agregar otros métodos aquí.

}
