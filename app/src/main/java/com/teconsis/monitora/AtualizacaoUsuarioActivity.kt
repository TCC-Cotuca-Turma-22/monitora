package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AtualizacaoUsuarioActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atualizacao_usuario)

        databaseHelper = DatabaseHelper(this)

        emailEditText = findViewById(R.id.novoEmailEditText)
        passwordEditText = findViewById(R.id.novaPasswordEditText)

        val atualizarButton: Button = findViewById(R.id.atualizarButton)

        val loggedInUserId = intent.getLongExtra("loggedInUserId", -1)

        Log.d("Validation", "loggedInUserId é $loggedInUserId")

        atualizarButton.setOnClickListener {
            val novoEmail = emailEditText.text.toString()
            val novaPassword = passwordEditText.text.toString()

            if (validateInput(loggedInUserId,novoEmail, novaPassword)) {
                // Obtenha o ID do usuário que deseja atualizar (você precisa implementar isso)

                try {
                    databaseHelper.updateUser(loggedInUserId, novoEmail, novaPassword)
                    Toast.makeText(this, "Usuário atualizado com sucesso", Toast.LENGTH_SHORT)
                        .show()
                    voltarParaPerfil()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(this, "Erro ao atualizar usuário", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Dados inválidos!", Toast.LENGTH_SHORT).show()
            }
        }

        val voltarButton: Button = findViewById(R.id.configButton)
        voltarButton.setOnClickListener {
            // Lógica para gravar os dados do usuário na tabela de usuários
            // após a gravação, retorne para a tela de login (MainActivity)
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (CadastroUsuarioActivity)
        }
    }

    private fun validateInput(loggedInUserId: Long, email: String, password: String): Boolean {
        return loggedInUserId >= 0 && isEmailValid(email) && isPasswordValid(password)
    }
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotEmpty() // Você pode adicionar regras adicionais aqui, se necessário
    }

    private fun voltarParaPerfil() {
        val intent = Intent(this, ConfiguracoesActivity::class.java)
        startActivity(intent)
        finish() // Encerra a atividade atual (AtualizacaoUsuarioActivity)
    }
}
