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

        loginButton.setOnClickListener {

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateLogin(email, password)) {
                if (!authenticateUser(email, password, this)) {
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

    private fun authenticateUser(email: String, password: String, context: Context): Boolean {
        val databaseHelper = DatabaseHelper(context) // Substitua 'context' pela instância do contexto apropriada
        val readableDatabase = databaseHelper.readableDatabase

        try {
            // Resto do código para autenticar o usuário
            // Você deve retornar o resultado da autenticação aqui
            println("Usuário autenticado")
            return true
        } catch (e: Exception) {
            println("Erro ao autenticar usuário: ${e.message}")
        } finally {
            // Agora você pode realizar operações no banco de dados usando 'readableDatabase'
            // Lembre-se de fechar o banco de dados quando terminar
            readableDatabase.close()
            databaseHelper.close()
        }
        return false
    }

    fun openCadastroUsuario(view: View) {
        val intent = Intent(this, CadastroUsuarioActivity::class.java)
        startActivity(intent)
    }

}


