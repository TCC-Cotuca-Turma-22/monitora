package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ConfiguracoesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        val gravarButton: Button = findViewById(R.id.gravarButton)

        gravarButton.setOnClickListener {
            // Lógica para gravar os dados do usuário na tabela de usuários
            // após a gravação, retorne para a tela de login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Encerra a atividade atual (CadastroUsuarioActivity)
        }
    }
}
