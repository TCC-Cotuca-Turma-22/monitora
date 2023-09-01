package com.teconsis.monitora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ListaUsuariosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        val userListJson = intent.getStringExtra("userListJson")
        val userListView = findViewById<ListView>(R.id.userListView)

        if (userListJson != null) {
            // Converte o JSON de volta para a lista de usuários
            val gson = Gson()
            val userListType = object : TypeToken<ArrayList<User>>() {}.type
            val userList = gson.fromJson<ArrayList<User>>(userListJson, userListType)

            // Use a lista de usuários como necessário
            // Por exemplo, configure um adaptador para exibi-la em um ListView
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, userList)
            userListView.adapter = adapter
        } else {
            // Lógica para lidar com o caso em que o JSON não está presente
        }



    }
}