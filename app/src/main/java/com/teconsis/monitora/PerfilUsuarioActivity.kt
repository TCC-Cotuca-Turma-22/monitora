package com.teconsis.monitora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PerfilUsuarioActivity : AppCompatActivity() {
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
    }
}
