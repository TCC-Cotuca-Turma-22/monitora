package com.teconsis.monitora

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.teconsis.monitora.databinding.ActivityConfigurarBinding
import kotlin.math.log

class ConfigurarActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityConfigurarBinding
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configurar)

        binding = ActivityConfigurarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        binding.gravarButton.setOnClickListener(this)

        binding.deleteButton.setOnClickListener(this)


        //esse log d esta aqui para poder ver os dados do banco pelo logcat
        Log.d("aparelhos", databaseHelper.getAllAparelhos().toString())



    }

    override fun onClick(view: View) {
        funAparelho(view)
    }

    private fun funAparelho(view: View){

        if(view.id == R.id.gravarButton){
            val descricao = binding.aparelhoEditText.text.toString()
            val codigoInfra = binding.codigoInfraEditText.text.toString()

            try {
                val insertedId = databaseHelper.insertAparelho(codigoInfra, descricao)

                if (insertedId != -1L) {
                    Toast.makeText(this, "APARELHO INSERIDO COM SUCESSO!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Falha ao inserir aparelho no banco de dados.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ConfigurarActivity", "Erro ao inserir aparelho: ${e.message}")
                Toast.makeText(this, "Erro ao inserir aparelho. Por favor, tente novamente.", Toast.LENGTH_SHORT).show()
            }



        }

        if (view.id == R.id.delete_button) {
            val idString = binding.idEditText.text.toString()

            if (idString.isNotEmpty()) {
                val id: Long = idString.toLong()
                val rowsAffected = databaseHelper.deletarAparelhoPorId(id)

                if (rowsAffected > 0) {
                    Log.d("ConfigurarActivity", "Aparelho deletado com sucesso")
                    Toast.makeText(this, "aparelho deletado com sucesso", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ConfigurarActivity", "Falha ao deletar aparelho")
                    Toast.makeText(this, "Falha ao deletar aparelho", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("ConfigurarActivity", "ID vazio")
                Toast.makeText(this, "ID vazio", Toast.LENGTH_SHORT).show()
            }
        }


    }



}
