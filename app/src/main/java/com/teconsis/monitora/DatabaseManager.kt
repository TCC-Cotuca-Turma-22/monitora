package com.teconsis.monitora

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseManager(private val databasePath: String) {
    private var connection: Connection? = null

    init {
        connect()
    }

    private fun connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")
            println("Conexão com o banco de dados estabelecida.")
        } catch (e: SQLException) {
            println("Erro ao conectar ao banco de dados: ${e.message}")
        }
    }

    fun getConnection(): Connection? {
        return connection
    }

    fun closeConnection() {
        connection?.close()
    }
}
