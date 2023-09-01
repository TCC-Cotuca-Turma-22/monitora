package com.teconsis.monitora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)
        val novoButton: Button = findViewById(R.id.novoButton)
        val forgotPasswordLink: TextView = findViewById(R.id.forgotPasswordLink)

        databaseHelper = DatabaseHelper(this)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateLogin(email, password)) {
                if (authenticateUser(email, password)) { // Altere aqui
                    val intent = Intent(this, ConfiguracoesActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Usuário inválido!", Toast.LENGTH_SHORT).show()
            }
        }

        novoButton.setOnClickListener {
            val intent = Intent(this, CadastroUsuarioActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordLink.setOnClickListener {
            // Lógica para lidar com a opção "Esqueceu a senha?"
        }
    }

    private fun validateLogin(email: String, password: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches() && password.isNotEmpty()
    }

    private fun authenticateUser(email: String, password: String): Boolean {
        val readableDatabase = databaseHelper.readableDatabase

        try {
            // Consulta SQL para verificar se o email e senha correspondem a um usuário
            val query = "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS} WHERE " +
                    "${DatabaseHelper.COLUMN_EMAIL} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?"

            val cursor = readableDatabase.rawQuery(query, arrayOf(email, password))
            cursor.moveToFirst()

            val count = cursor.getInt(0)
            return count > 0 // Se count for maior que 0, o usuário está autenticado
        } catch (e: Exception) {
            println("Erro ao autenticar usuário: ${e.message}")
        } finally {
            readableDatabase.close()
        }
        return false
    }
}


