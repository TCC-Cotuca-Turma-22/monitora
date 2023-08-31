package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class CadastroUsuarioActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userListView: ListView
    private lateinit var showUsersButton: Button

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)

        val gravarButton: Button = findViewById(R.id.gravarButton)
        val voltarButton: Button = findViewById(R.id.retornarButton)

        voltarButton.setOnClickListener {
            // Lógica para gravar os dados do usuário na tabela de usuários
            // após a gravação, retorne para a tela de login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (CadastroUsuarioActivity)
        }

        gravarButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val databaseManager = DatabaseManager("monitora.db")
            val databaseOperations = DatabaseOperations(databaseManager)

            val addUserSuccess = databaseOperations.addUser(email, password)

            if (addUserSuccess) {
                // Usuário adicionado com sucesso, redirecione para a tela de login
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Encerra a atividade atual (CadastroUsuarioActivity)
            } else {
                // Lida com a falha ao adicionar o usuário (você pode exibir uma mensagem de erro aqui)
                println("Erro ao adicionar usuário")
            }
        }

        databaseHelper = DatabaseHelper(this)

        userListView = findViewById(R.id.userListView)
        showUsersButton = findViewById(R.id.showUsersButton)

        showUsersButton.setOnClickListener {
            // Chame o método getAllUsers
            val userList = databaseHelper.getAllUsers()

            // Crie um adapter para exibir a lista de usuários no ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
            userListView.adapter = adapter
        }
    }
}
