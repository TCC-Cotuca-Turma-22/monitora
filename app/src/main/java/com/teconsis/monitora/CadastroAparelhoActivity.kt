package com.teconsis.monitora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CadastroAparelhoActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var aparelhoEditText: EditText
    private lateinit var codigoInfraEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_aparelho)

        databaseHelper = DatabaseHelper(this)
        val gravarButton: Button = findViewById(R.id.gravarButton)
        val listarButton: Button = findViewById(R.id.listButton)

        aparelhoEditText = findViewById(R.id.aparelhoEditText)
        codigoInfraEditText = findViewById(R.id.codigoInfraEditText)

        gravarButton.setOnClickListener {
            val aparelho = aparelhoEditText.text.toString()
            val codigoInfra = codigoInfraEditText.text.toString()

                try {
                    databaseHelper.insertAparelho(codigoInfra, aparelho)
                    Toast.makeText(this, "Aparelho adicionado com sucesso", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: IllegalArgumentException) {
                    Toast.makeText(
                        this,
                        "Erro ao adicionar Aparelho: ${e.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }

        listarButton.setOnClickListener{
            val intent = Intent(this, ListaAparelhosActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bluetoothButton = findViewById<Button>(R.id.bluetoothButton)
        bluetoothButton.setOnClickListener {
            val intent = Intent(this, BluetoothActivity::class.java)
            startActivity(intent)
        }

    }
}