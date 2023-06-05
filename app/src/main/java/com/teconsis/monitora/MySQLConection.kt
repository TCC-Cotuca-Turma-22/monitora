package com.teconsis.monitora

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
class MySQLConnection(host: String, bd: String, usr: String, pass: String) {
    private val HOST = host
    private val PORT = 3306
    private val DATABASE = bd
    private val USERNAME = usr
    private val PASSWORD = pass

    companion object {
        @Volatile
        private var instance: MySQLConnection? = null

        fun getInstance(): MySQLConnection {
            return instance ?: synchronized(this) {
                instance ?: MySQLConnection(
                    "regulus.cotuca.unicamp.br",
                     "BD22597",
                    "BD22597",
                    "BD22597"
                ).also { instance = it }
            }
        }
    }

    fun getConnection(): Connection? {
        val url = "jdbc:mysql://$HOST:$PORT/$DATABASE"+"?allowPublicKeyRetrieval=true"
        val username = "BD22597"
        val password = "BD22597"

        return try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            DriverManager.getConnection(url, username, password)
        } catch (e: ClassNotFoundException) {
            println("Driver JDBC do MySQL não encontrado.")
            null
        } catch (e: SQLException) {
            println("Erro ao conectar ao banco de dados: " + e.message)
            null
        }
    }

    fun createTableIfNotExists() {
        val createTableSQL = ("CREATE TABLE IF NOT EXISTS USUARIOS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "login VARCHAR(255) NOT NULL,"
                + "senha VARCHAR(255) NOT NULL"
                + ")")

        val insertUserSQL = "INSERT INTO USUARIOS (login, senha) VALUES (?, ?)"

        try {
            val connection = getConnection()
            try {
                connection.use { conn ->
                    conn?.createStatement()?.use { statement ->
                        // Criar tabela USUARIOS se não existir
                        statement.executeUpdate(createTableSQL)
                        println("Tabela USUARIOS criada ou já existe.")

                        // Verificar se o usuário teste já existe
                        val checkUserSQL = "SELECT COUNT(*) FROM USUARIOS WHERE login = ?"
                        val checkUserStatement = conn.prepareStatement(checkUserSQL)
                        checkUserStatement.setString(1, "teste")
                        val resultSet = checkUserStatement.executeQuery()

                        resultSet.next()
                        val userCount = resultSet.getInt(1)

                        if (userCount == 0) {
                            // Inserir usuário teste com senha 1234
                            val insertUserStatement = conn.prepareStatement(insertUserSQL)
                            insertUserStatement.setString(1, "teste")
                            insertUserStatement.setString(2, "1234")
                            insertUserStatement.executeUpdate()
                            println("Usuário teste inserido com sucesso.")
                        } else {
                            println("Usuário teste já existe.")
                        }
                    }
                }
            } finally {
                connection?.close()
            }
        } catch (e: SQLException) {
            println("Erro ao criar tabela USUARIOS: " + e.message)
        }
    }

    fun autenticarUsuario(email: String, senha: String): Boolean {
        val sql = "SELECT COUNT(*) AS count FROM USUARIOS WHERE login = ? AND senha = ?"
        return try {
            getConnection()?.use { connection ->
                connection.prepareStatement(sql).use { statement ->
                    statement.setString(1, email)
                    statement.setString(2, senha)
                    val resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        val count = resultSet.getInt("count")
                        count > 0
                    } else {
                        false
                    }
                }
            } ?: false
        } catch (e: SQLException) {
            println("Erro ao autenticar usuário: " + e.message)
            false
        }
    }


    fun testDatabaseConnection() {
        val host = "regulus.cotuca.unicamp.br"
        val port = 3306
        val database = "BD22597"
        val username = "BD22597"
        val password = "BD22597"

        val url = "jdbc:mysql://$host:$port/$database"

        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            val connection = DriverManager.getConnection(url, username, password)
            println("Conexão com o banco de dados estabelecida com sucesso.")
            // Faça algo com a conexão, se necessário
            connection.close()
        } catch (e: ClassNotFoundException) {
            println("Driver JDBC do MySQL não encontrado.")
        } catch (e: SQLException) {
            println("testeDatabaseConnection...Erro ao conectar ao banco de dados: ${e.message}")
        }
    }


}
