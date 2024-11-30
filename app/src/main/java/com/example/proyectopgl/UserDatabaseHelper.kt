package com.example.proyectopgl

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDatabaseHelper(context: Context, DATABASE_NAME: String?, DATABASE_VERSION: Int) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Define la creación de la tabla (cuando se crea por primera vez la base de datos)
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE Users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL
            );
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    // Método para obtener todos los usuarios
    fun getAllUsers(): List<User> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Users", null)

        val users = mutableListOf<User>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex("id"))
            val username = cursor.getString(cursor.getColumnIndex("username"))
            val email = cursor.getString(cursor.getColumnIndex("email"))
            val password = cursor.getString(cursor.getColumnIndex("password"))

            val user = User(id, username, email, password)
            users.add(user)
        }
        cursor.close()
        return users
    }

    // Insertar un nuevo usuario
    fun insertUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("password", user.password)
        }

        return db.insert("Users", null, values)
    }
}
