package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ListaUsuariosActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        databaseHelper = DatabaseHelper(this)

        val userListJson = intent.getStringExtra("userListJson")
        val userListView = findViewById<ListView>(R.id.userListView)
        val errorTextView = findViewById<TextView>(R.id.errorTextView)
        val voltarButton: Button = findViewById(R.id.returnButton)
        val deleteButton: Button = findViewById(R.id.deleteButton)
        val loggedInUserEmail = intent.getStringExtra("loggedInUserEmail")
        val loggedInUserRole = loggedInUserEmail?.let { databaseHelper.getRoleUser(it) }
        // Pega o email do usuário logado
        val userIdEditTextContainer = findViewById<LinearLayout>(R.id.userIdEditTextContainer)

        voltarButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Se o usuário logado for administrador mostra a lista de usuários
        if (loggedInUserRole == "admin") {
            if (userListJson != null) {
                // Converte o JSON de volta para a lista de usuários
                val gson = Gson()
                val userListType = object : TypeToken<ArrayList<User>>() {}.type
                val userList = gson.fromJson<ArrayList<User>>(userListJson, userListType)

                userListView.visibility = View.VISIBLE
                errorTextView.visibility = View.GONE
                userIdEditTextContainer.visibility = View.VISIBLE
                deleteButton.isEnabled = true
                // Exibi em um ListView
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
                userListView.adapter = adapter
            } else {
                userListView.visibility = View.GONE
                errorTextView.visibility = View.VISIBLE
                userIdEditTextContainer.visibility = View.GONE
                deleteButton.isEnabled = false

            }
        } else {
            userListView.visibility = View.GONE
            errorTextView.visibility = View.VISIBLE
            userIdEditTextContainer.visibility = View.GONE
            deleteButton.isEnabled = false

        }

        deleteButton.setOnClickListener {

            val userIdEditText = findViewById<EditText>(R.id.userIdEditText)
            val userIdToDelete = userIdEditText.text.toString().toLongOrNull()
            val userEmail = intent.getStringExtra("loggedInUserEmail")

            try {
                if (userIdToDelete != null) {
                    // Chama a função para excluir o usuário do DatabaseHelper
                    val rowsDeleted = userEmail?.let { it1 ->
                        databaseHelper.deleteUserById(it1, userIdToDelete)
                    }

                    if (rowsDeleted != null) {
                        if (rowsDeleted > 0) {
                            // A exclusão foi bem-sucedida
                            // Atualiza a lista de usuários e o adaptador
                            val userList = databaseHelper.getAllUsers()
                            val adapter =
                                ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
                            userListView.adapter = adapter
                        } else {
                            Toast.makeText(this, "Usuário não existe.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "ID não pode ser vazio.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(
                    this,
                    "Usuário não é administrador: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}