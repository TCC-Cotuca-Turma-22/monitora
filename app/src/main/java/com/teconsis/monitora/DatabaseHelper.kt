package com.teconsis.monitora

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "monitora.db", null, 1) {

    companion object {
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EMAIL TEXT UNIQUE," +
                "$COLUMN_PASSWORD TEXT)"

        db?.execSQL(createUserTableQuery)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Atualize o esquema do banco de dados, se necessário
    }

    fun insertUser(email: String, password: String): Long {
        val values = ContentValues()
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email e senha não podem estar vazios")
        }
        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)

        val db = writableDatabase
        return db.insert(TABLE_USERS, null, values)
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS"

        val cursor = db.rawQuery(query, null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
                val email = it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL))
                val password = it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD))

                val user = User(id, email, password)
                userList.add(user)
            }
        }

        return userList
    }
}
