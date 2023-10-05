package com.teconsis.monitora

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class ConfigurarActivity : AppCompatActivity() {

    private lateinit var aparelhoEditText: EditText
    private lateinit var temporizadorEditText: EditText
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar)

        databaseHelper = DatabaseHelper(this)
        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val loggedInUserId = sharedPreferences.getLong("loggedInUserId", -1)
       // aparelhoEditText = findViewById(R.id.aparelhoEditText)
        temporizadorEditText = findViewById(R.id.temporizadorEditText)
        val gravarButton: Button = findViewById(R.id.gravarButton)
        val retornarButton: Button = findViewById(R.id.retornarButton)

        val aparelhoSpinner: Spinner = findViewById(R.id.aparelhoSpinner)
        val aparelhoList = databaseHelper.getAllAparelhos()
        val aparelhoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            aparelhoList.map { it.descricao })
        aparelhoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        aparelhoSpinner.adapter = aparelhoAdapter

        gravarButton.setOnClickListener {
            val temporizador = temporizadorEditText.text.toString().toIntOrNull() ?: 0

            temporizadorEditText.text.clear()

            val db = DatabaseHelper(this).writableDatabase
            val values = ContentValues()

            values.put(DatabaseHelper.COLUMN_ID_USUARIO, loggedInUserId)
            values.put(DatabaseHelper.COLUMN_ID_DISPOSITIVO, 1)
            values.put(DatabaseHelper.COLUMN_ID_MODO_OPERACAO, 1)
            values.put(DatabaseHelper.COLUMN_TEMPORIZADOR, temporizador)

            val configuracaoId = db.insert(DatabaseHelper.TABLE_CONFIGURACAO_USUARIO, null, values)

// Verifique se a inserção foi bem-sucedida
            if (configuracaoId != -1L) {
                // Inserção bem-sucedida
            } else {
                // Falha na inserção
            }

        }



        retornarButton.setOnClickListener {
            val intent = Intent(this, ConfiguracoesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
