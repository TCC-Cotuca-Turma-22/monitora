package com.teconsis.monitora

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class DatabaseOperations(private val databaseManager: DatabaseManager) {

    fun executeQuery(query: String): ResultSet? {
        val connection: Connection? = databaseManager.getConnection()
        var resultSet: ResultSet? = null

        try {
            val statement: PreparedStatement = connection?.prepareStatement(query) ?: return null
            resultSet = statement.executeQuery()
        } catch (e: SQLException) {
            println("Erro ao executar consulta: ${e.message}")
        }

        return resultSet
    }

    fun authenticateUser(email: String, password: String): Boolean {
        val query = "SELECT COUNT(*) as count FROM users WHERE email = ? AND password = ?"
        val connection: Connection? = databaseManager.getConnection()

        try {
            val statement: PreparedStatement = connection?.prepareStatement(query) ?: return false
            statement.setString(1, email)
            statement.setString(2, password)

            val resultSet: ResultSet = statement.executeQuery()
            resultSet.next()
            val count = resultSet.getInt("count")

            return count > 0
        } catch (e: SQLException) {
            println("Erro ao autenticar usuário: ${e.message}")
        }

        return false
    }

    fun addUser(email: String, password: String): Boolean {
        val query = "INSERT INTO users (email, password) VALUES (?, ?)"
        val connection: Connection? = databaseManager.getConnection()

        try {
            val statement: PreparedStatement = connection?.prepareStatement(query) ?: return false
            statement.setString(1, email)
            statement.setString(2, password)

            val rowsAffected = statement.executeUpdate()

            return rowsAffected > 0
        } catch (e: SQLException) {
            println("Erro ao adicionar usuário: ${e.message}")
        }

        return false
    }
    // Adicione mais métodos para outras operações no banco de dados, como inserção, atualização, etc.
}
