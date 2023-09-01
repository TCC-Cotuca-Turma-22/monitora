package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class CadastroUsuarioActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var showUsersButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)

        databaseHelper = DatabaseHelper(this)

        val gravarButton: Button = findViewById(R.id.gravarButton)
        val voltarButton: Button = findViewById(R.id.retornarButton)

        voltarButton.setOnClickListener {
            // Lógica para gravar os dados do usuário na tabela de usuários
            // após a gravação, retorne para a tela de login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (CadastroUsuarioActivity)
        }


        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        gravarButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            try {
                val userId = databaseHelper.insertUser(email, password)
                Toast.makeText(this, "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Erro ao adicionar usuário", Toast.LENGTH_SHORT).show()
            }

        }

        showUsersButton = findViewById(R.id.showUsersButton)

        showUsersButton.setOnClickListener {
            // Chame o método getAllUsers
            val userList = databaseHelper.getAllUsers()

            // Converte a lista de usuários em JSON
            val gson = Gson()
            val userListJson = gson.toJson(userList)

            // Crie uma Intent para iniciar a ListaUsuariosActivity e passe o JSON como um extra
            val intent = Intent(this, ListaUsuariosActivity::class.java)
            intent.putExtra("userListJson", userListJson)
            startActivity(intent)
        }

    }
}
