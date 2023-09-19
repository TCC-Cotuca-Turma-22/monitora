package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
        databaseHelper.createAdminUser()

        //databaseHelper.insertAparelho("546146","tv sala")

        //databaseHelper.deletarAparelhoPorId(1)

        databaseHelper.updateAparelho(2,"64565546","tv quarto")
        var aparelhoslist = databaseHelper.getAllAparelhos()

        Log.d("aparelhos1",aparelhoslist.toString())


        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateLogin(email, password)) {
                val loggedInUserId = databaseHelper.authenticateUser(email, password)
                if (loggedInUserId != null) {
                    val intentPerfil = Intent(this, PerfilUsuarioActivity::class.java)
                    intentPerfil.putExtra("loggedInUserId", loggedInUserId)
                    intentPerfil.putExtra("loggedInUserEmail", email)
                    startActivity(intentPerfil)
                } else {
                    Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, insira um endereço de e-mail válido e uma senha.",
                    Toast.LENGTH_SHORT
                ).show()
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
}


