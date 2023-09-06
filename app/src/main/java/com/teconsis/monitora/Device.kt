package com.teconsis.monitora

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class Device
    (
    val id: Int,
    val descricao: String,
    val id_aparelho: Int
)
{
    object ContratoDispositivo {
        // Nome da tabela
        const val TABLE_NAME = "dispositivo"

        // Nomes das colunas
        object Columns {
            const val ID = "id"
            const val DESCRICAO = "descricao"
            const val ID_APARELHO = "id_aparelho"
        }
    }
    fun insertDevice(device: Device) {
        val values = ContentValues()
        values.put(ContratoDispositivo.Columns.DESCRICAO, device.descricao)
        values.put(ContratoDispositivo.Columns.ID_APARELHO, device.id_aparelho)

        val writableDatabase = null
        val db = writableDatabase
        try {
            val result = db.insert(ContratoDispositivo.TABLE_NAME, null, values)
            if (result == -1L) {
                // A inserção falhou
                // Lide com o erro aqui
            } else {
                // Inserção bem-sucedida
            }
        } catch (e: Exception) {
            // Trate qualquer exceção que possa ocorrer durante a inserção
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    // No seu DataBaseHelper.onCreate()
    fun onCreate(db: SQLiteDatabase?) {
        // Cria a tabela "dispositivo"
        val createTableSQL = """
        CREATE TABLE ${ContratoDispositivo.TABLE_NAME} (
            ${ContratoDispositivo.Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${ContratoDispositivo.Columns.DESCRICAO} TEXT NOT NULL,
            ${ContratoDispositivo.Columns.ID_APARELHO} INTEGER NOT NULL
        )
    """.trimIndent()

        db?.execSQL(createTableSQL)
    }
    }