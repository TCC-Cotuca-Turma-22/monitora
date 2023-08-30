package com.teconsis.monitora

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "monitora.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Crie tabelas e defina a estrutura do banco de dados aqui
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Atualize o esquema do banco de dados, se necessário
    }
}
