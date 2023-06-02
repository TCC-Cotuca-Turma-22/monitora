package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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

            // remover comentários e ajustar para executar no real //
            /*if (validateLogin(email, password)) {
                // Dados de login válidos, realizar a autenticação no servidor SQLServer
                authenticateUser(email, password)
            } else {
                Toast.makeText(this, "Usuário inválido!", Toast.LENGTH_SHORT).show()
            }*/
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
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

    private fun authenticateUser(email: String, password: String) {
        // Lógica para autenticar o usuário no servidor SQLServer
        // Utilize as informações fornecidas (BD22529, regulus.cotuca.unicamp.br, BD22529, BD22529)
        // Exiba a terceira tela (InstrucoesActivity) se a autenticação for bem-sucedida
        val intent = Intent(this, ConfigurarActivity::class.java)
        startActivity(intent)
        finish() // Encerra a atividade atual (MainActivity)
    }
}
