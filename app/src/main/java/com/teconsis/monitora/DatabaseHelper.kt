package com.teconsis.monitora

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "monitora.db", null, 1) {

    companion object {
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUserTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_USERS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_EMAIL TEXT UNIQUE," +
                "$COLUMN_PASSWORD TEXT," +
                "$COLUMN_ROLE TEXT)"

        db?.execSQL(createUserTableQuery)
    }

    fun createAdminUser() {
        val adminEmail = "admin@admin.com"
        val adminPassword = "admin"
        val adminRole = "admin"

        val db = writableDatabase

        // Verifique se o usuário administrador já existe
        if (!isEmailExists(adminEmail)) {
            val values = ContentValues()

            values.put(COLUMN_EMAIL, adminEmail)
            values.put(COLUMN_PASSWORD, adminPassword)
            values.put(COLUMN_ROLE, adminRole)

            db.insert(TABLE_USERS, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Atualize o esquema do banco de dados, se necessário
    }

    fun insertUser(email: String, password: String): Long {
        val values = ContentValues()
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email e senha não podem estar vazios")
        }

        // Verifique se o email já existe no banco de dados
        if (isEmailExists(email)) {
            throw IllegalArgumentException("este email já está em uso")
        }

        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)

        val db = writableDatabase
        return db.insert(TABLE_USERS, null, values)
    }

    private fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"

        // Verifique se o email é diferente do email do usuário administrador
        val isAdminEmail = "admin@example.com"
        val args = if (email == isAdminEmail) {
            arrayOf()
        } else {
            arrayOf(email)
        }

        val cursor = db.rawQuery(query, arrayOf(email))

        cursor.use {
            if (it.moveToFirst()) {
                val count = it.getInt(0)
                return count > 0
            }
        }

        return false
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS"

        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))

                val user = User(id, email, password)
                userList.add(user)
            }
        } catch (e: Exception) {
        } finally {
            cursor.close()
        }

        return userList
    }

    fun updateUser(id: Long, novoEmail: String, novoPassword: String): Int {
        val values = ContentValues()
        if (novoEmail.isEmpty() || novoPassword.isEmpty()) {
            throw IllegalArgumentException("Novo e-mail e senha não podem estar vazios")
        }
        values.put(COLUMN_EMAIL, novoEmail)
        values.put(COLUMN_PASSWORD, novoPassword)

        val db = writableDatabase
        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteUserById(adminEmail: String, userId: Long): Int {
        val adminRole = getRoleUser(adminEmail)

        if (adminRole == "admin") {
            val db = writableDatabase
            return db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        } else {
            // O usuário logado não é um administrador, não permita a exclusão de usuários
            throw SecurityException("Apenas administradores podem excluir usuários")
        }
    }

    fun getRoleUser(email: String): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ROLE FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            }
        } finally {
            cursor.close()
        }

        return null // Retorna null se a função não for encontrada
    }
}
