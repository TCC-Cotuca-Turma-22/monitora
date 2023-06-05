package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
class ConfiguracoesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val ligaDesligaButton: Button = findViewById(R.id.ligaDesligaButton)
        val configurarButton: Button = findViewById(R.id.configurarButton)
        val retornarButton: Button = findViewById(R.id.retornarButton)

        ligaDesligaButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (ConfiguracoesActivity)
        }

        configurarButton.setOnClickListener {
            val intent = Intent(this, ConfigurarActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (ConfiguracoesActivity)
        }

        retornarButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (ConfiguracoesActivity)
        }
    }
}
