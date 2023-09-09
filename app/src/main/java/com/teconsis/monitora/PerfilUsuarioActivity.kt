    package com.teconsis.monitora

    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log
    import android.widget.Button
    import com.google.gson.Gson

    class PerfilUsuarioActivity : AppCompatActivity() {
        private lateinit var showUsersButton: Button
        private lateinit var databaseHelper: DatabaseHelper

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_perfil_usuario)

            val atualizarPerfilButton: Button = findViewById(R.id.atualizarPerfilButton)

            atualizarPerfilButton.setOnClickListener {
                val intent = Intent(this, AtualizacaoUsuarioActivity::class.java)
                startActivity(intent)
            }

            val voltarButton: Button = findViewById(R.id.configButton)
            voltarButton.setOnClickListener {
                // Lógica para gravar os dados do usuário na tabela de usuários
                // após a gravação, retorne para a tela de login (MainActivity)
                val intent = Intent(this, ConfiguracoesActivity::class.java)
                startActivity(intent)
                finish() // Encerra a atividade atual (CadastroUsuarioActivity)
            }

            databaseHelper = DatabaseHelper(this)
            showUsersButton = findViewById(R.id.showUsersButton)

            showUsersButton.setOnClickListener {
                // Chame o método getAllUsers
                val userList = databaseHelper.getAllUsers()

                // Converte a lista de usuários em JSON
                val gson = Gson()
                val userListJson = gson.toJson(userList)
                val loggedInUserEmail = intent.getStringExtra("loggedInUserEmail")

                // Crie uma Intent para iniciar a ListaUsuariosActivity e passe o JSON como um extra
                val intent = Intent(this, ListaUsuariosActivity::class.java)
                intent.putExtra("userListJson", userListJson)
                intent.putExtra("loggedInUserEmail", loggedInUserEmail)
                startActivity(intent)
            }
        }
    }
