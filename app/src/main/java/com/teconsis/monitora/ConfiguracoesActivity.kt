package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class ConfiguracoesActivity : AppCompatActivity() {
    private lateinit var modoOperacao: ModoOperacao
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes)

        databaseHelper = DatabaseHelper(this)

        val ligaDesligaButton = findViewById<ToggleButton>(R.id.ligaDesligaButton)
        val configurarButton: Button = findViewById(R.id.configurarButton)
        val retornarButton: Button = findViewById(R.id.retornarButton)

      ligaDesligaButton.setOnCheckedChangeListener { buttonView, isChecked ->
        modoOperacao = ModoOperacao.ligarDesligar(1, isChecked)
        databaseHelper.atualizarModoOperacao(modoOperacao)
          val listaDisp = databaseHelper.getAllModoOperacoes()
          Log.d("ListaSisp", listaDisp.toString())

      }
        val listaAp = databaseHelper.getAllAparelhos()
        Log.d("Lista", listaAp.toString())
        configurarButton.setOnClickListener {
            val intent = Intent(this, ConfigurarActivity::class.java)
            startActivity(intent)
            finish()
        }

        retornarButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
