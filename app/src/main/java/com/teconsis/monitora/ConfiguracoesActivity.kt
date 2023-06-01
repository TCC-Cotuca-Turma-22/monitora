package com.teconsis.monitora

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ConfiguracaoActivity : AppCompatActivity() {

    private lateinit var aparelhoEditText: EditText
    private lateinit var temporizadorEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        aparelhoEditText = findViewById(R.id.aparelhoEditText)
        temporizadorEditText = findViewById(R.id.temporizadorEditText)
        val gravarButton: Button = findViewById(R.id.gravarButton)

        gravarButton.setOnClickListener {
            val aparelho = aparelhoEditText.text.toString()
            val temporizador = temporizadorEditText.text.toString()

            // Lógica para gravar os dados de aparelho e temporizador na tabela DISPOSITIVOS
            // e adicioná-los à lista de dados

            // Limpar os campos de texto após a gravação
            aparelhoEditText.text.clear()
            temporizadorEditText.text.clear()
        }
    }
}
