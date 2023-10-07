package com.teconsis.monitora

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "monitora.db", null, 1) {

    companion object {
        const val TABLE_USERS = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_ROLE = "role"

        const val TABLE_MODO_OPS = "modo_operacao"
        const val COLUMN_ID_MOD = "id"
        const val COLUMN_DESCRICAO_MOD = "descricao"
        const val COLUMN_MODO_OP = "modo_operacao"

        const val TABLE_APARELHOS = "aparelhos"
        const val COLUMN_ID_APARELHO = "id"
        const val COLUMN_CODIGO_INFRA = "codigo_infra"
        const val COLUMN_DESCRICAO_AP = "descricao"

        const val TABLE_DISPOSITIVOS = "dispositivos"
        const val COLUMN_ID_DISP = "id"
        const val COLUMN_DESCRICAO_DISP = "descricao"
        const val COLUMN_ID_APARELHO_FK = "id_aparelho"

        const val TABLE_CONFIGURACAO_USUARIO = "configuracao_usuario"
        const val COLUMN_ID_USUARIO = "id_usuario"
        const val COLUMN_ID_DISPOSITIVO = "id_dispositivo"
        const val COLUMN_ID_MODO_OPERACAO = "id_modo_operacao"
        const val COLUMN_TEMPORIZADOR = "temporizador"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.beginTransaction()

        try {
            val createUserTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_USERS(" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_EMAIL TEXT UNIQUE," +
                    "$COLUMN_PASSWORD TEXT," +
                    "$COLUMN_ROLE TEXT)"

            db?.execSQL(createUserTableQuery)

            val createModoOperacaoTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_MODO_OPS(" +
                    "$COLUMN_ID_MOD INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_DESCRICAO_MOD TEXT," +
                    "$COLUMN_MODO_OP INTEGER)"

            db?.execSQL(createModoOperacaoTableQuery)

            val createAparelhoTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_APARELHOS(" +
                    "$COLUMN_ID_APARELHO INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_CODIGO_INFRA TEXT," +
                    "$COLUMN_DESCRICAO_AP TEXT)"

            db?.execSQL(createAparelhoTableQuery)

            val createDispositivoTableQuery = "CREATE TABLE IF NOT EXISTS $TABLE_DISPOSITIVOS(" +
                    "$COLUMN_ID_DISP INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_DESCRICAO_DISP TEXT," +
                    "$COLUMN_ID_APARELHO_FK INTEGER," +
                    "FOREIGN KEY ($COLUMN_ID_APARELHO_FK) REFERENCES $TABLE_APARELHOS($COLUMN_ID_APARELHO)" +
                    ")"

            db?.execSQL(createDispositivoTableQuery)

            val createConfiguracaoUsuarioTableQuery =
                "CREATE TABLE IF NOT EXISTS $TABLE_CONFIGURACAO_USUARIO(" +
                        "$COLUMN_ID_USUARIO INTEGER NOT NULL," +
                        "$COLUMN_ID_DISPOSITIVO INTEGER NOT NULL," +
                        "$COLUMN_ID_MODO_OPERACAO INTEGER NOT NULL," +
                        "$COLUMN_TEMPORIZADOR INTEGER," +
                        "FOREIGN KEY ($COLUMN_ID_USUARIO) REFERENCES $TABLE_USERS($COLUMN_ID)," +
                        "FOREIGN KEY ($COLUMN_ID_DISPOSITIVO) REFERENCES $TABLE_DISPOSITIVOS($COLUMN_ID_DISP)," +
                        "FOREIGN KEY ($COLUMN_ID_MODO_OPERACAO) REFERENCES $TABLE_MODO_OPS($COLUMN_ID_MOD)" +
                        ")"

            db?.execSQL(createConfiguracaoUsuarioTableQuery)
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    fun createAdminUser() {
        val adminEmail = "admin@admin.com"
        val adminPassword = "admin"
        val adminRole = "admin"

        val db = writableDatabase

        // Verifique se o usuário administrador já existe
        if (!isEmailExists(adminEmail)) {
            val values = ContentValues()

            values.put(COLUMN_EMAIL, adminEmail)
            values.put(COLUMN_PASSWORD, adminPassword)
            values.put(COLUMN_ROLE, adminRole)

            db.insert(TABLE_USERS, null, values)
        }
    }

    fun createModoOperacaoPadrao() {
        val db = writableDatabase

        if (!modoOperacaoExiste(db)) {
            val modoDesligadoValues = ContentValues()
            modoDesligadoValues.put(COLUMN_DESCRICAO_MOD, "Desligado")
            modoDesligadoValues.put(COLUMN_MODO_OP, 0)
            db?.insert(TABLE_MODO_OPS, null, modoDesligadoValues)
        }
    }

    fun createAparelhoPadrao() {
        val db = writableDatabase

        val aparelhos = listOf(
            "TV Sala",
            "TV Quarto"
        )

        if (!aparelhoExiste(db)) {
            for (descricao in aparelhos) {
                val values = ContentValues()
                values.put(COLUMN_DESCRICAO_AP, descricao)
                values.putNull(COLUMN_CODIGO_INFRA)
                db.insert(TABLE_APARELHOS, null, values)
            }
        }
    }

    fun createDispositivoPadrao() {
        val db = writableDatabase

        val dispositivo = "SmartOff"

        if (!dispositivoExiste(db)) {
            val contentValues = ContentValues()
            contentValues.put(COLUMN_DESCRICAO_DISP, dispositivo)
            contentValues.putNull(COLUMN_ID_APARELHO_FK)
            db.insert(TABLE_DISPOSITIVOS, null, contentValues)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Atualize o esquema do banco de dados, se necessário
    }

    fun insertUser(email: String, password: String): Long {
        val values = ContentValues()
        if (email.isEmpty() || password.isEmpty()) {
            throw IllegalArgumentException("Email e senha não podem estar vazios")
        }

        // Verifica se o email já existe no banco de dados
        if (isEmailExists(email)) {
            throw IllegalArgumentException("esse email já está em uso")
        }

        values.put(COLUMN_EMAIL, email)
        values.put(COLUMN_PASSWORD, password)

        val db = writableDatabase
        return db.insert(TABLE_USERS, null, values)
    }

    private fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        cursor.use {
            if (it.moveToFirst()) {
                val count = it.getInt(0)
                return count > 0
            }
        }
        return false
    }

    fun getAllUsers(): List<User> {
        val userList = mutableListOf<User>()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS"

        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
                val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))

                val user = User(id, email, password)
                userList.add(user)
            }
        } catch (e: Exception) {
            Log.e("TAG", "Erro ao recuperar todos os usuários: ${e.message}")
        } finally {
            cursor.close()
        }
        return userList
    }

    fun updateUser(id: Long, novoEmail: String, novoPassword: String): Int {
        val values = ContentValues()
        if (novoEmail.isEmpty() || novoPassword.isEmpty()) {
            throw IllegalArgumentException("Novo e-mail e senha não podem estar vazios")
        }
        values.put(COLUMN_EMAIL, novoEmail)
        values.put(COLUMN_PASSWORD, novoPassword)

        val db = writableDatabase
        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    fun updateUserByEmail(email: String, novoPassword: String): Int {
        val values = ContentValues()

        values.put(COLUMN_PASSWORD, novoPassword)

        val db = writableDatabase
        return db.update(
            TABLE_USERS,
            values,
            "$COLUMN_EMAIL = ?",
            arrayOf(email)
        )
    }

    fun deleteUserById(adminEmail: String, userId: Long): Int {
        val adminRole = getRoleUser(adminEmail)

        if (adminRole == "admin") {
            val db = writableDatabase
            return db.delete(TABLE_USERS, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        } else {
            // O usuário logado não é um administrador, não permita a exclusão de usuários
            throw SecurityException("Apenas administradores podem excluir usuários")
        }
    }

    fun getRoleUser(email: String): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN_ROLE FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))

        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE))
            }
        } finally {
            cursor.close()
        }
        return null // Retorna null se a função não for encontrada
    }

    fun authenticateUser(email: String, password: String): Long? {
        var userId: Long? = null

        val query =
            "SELECT ${DatabaseHelper.COLUMN_ID} FROM ${DatabaseHelper.TABLE_USERS} WHERE " +
                    "${DatabaseHelper.COLUMN_EMAIL} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?"

        val cursor = readableDatabase.rawQuery(query, arrayOf(email, password))

        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
        }

        cursor.close()
        readableDatabase.close()

        return userId
    }

    private fun isCodigoInfraAndNameExists(codigoInfra: String, descricao: String): Boolean {
        val db = readableDatabase
        val query =
            "SELECT COUNT(*) FROM $TABLE_APARELHOS WHERE $COLUMN_CODIGO_INFRA = ? AND $COLUMN_DESCRICAO_AP = ?"
        val cursor = db.rawQuery(query, arrayOf(codigoInfra, descricao))

        cursor.use {
            if (it.moveToFirst()) {
                val count = it.getInt(0)
                return count > 0
            }
        }
        return false
    }

    fun insertAparelho(codigoInfra: String, descricao: String): Long {
        val values = ContentValues()

        // Verifique se o código de infra já existe no banco de dados
        if (isCodigoInfraAndNameExists(codigoInfra, descricao)) {
            throw IllegalArgumentException("Este código de infra já está em uso")
        }

        values.put(COLUMN_CODIGO_INFRA, codigoInfra)
        values.put(COLUMN_DESCRICAO_AP, descricao)

        val db = writableDatabase
        return db.insert(TABLE_APARELHOS, null, values)
    }


    fun deletarAparelhoPorId(aparelhoId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_APARELHOS, "$COLUMN_ID_APARELHO = ?", arrayOf(aparelhoId.toString()))
    }

    fun getAllAparelhos(): List<Aparelho> {
        val aparelhoList = mutableListOf<Aparelho>()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_APARELHOS"

        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_APARELHO))
                val codigoInfra =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CODIGO_INFRA))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO_AP))

                val aparelho = Aparelho(id, codigoInfra, descricao)
                aparelhoList.add(aparelho)
            }
        } catch (e: Exception) {
            Log.e("TAG", "Erro ao recuperar todos os aparelhos: ${e.message}")
        } finally {
            cursor.close()
        }
        return aparelhoList
    }

    fun updateAparelho(id: Int, novoCodigo: String, novaDescricao: String): Int {
        val values = ContentValues()

        values.put(COLUMN_CODIGO_INFRA, novoCodigo)
        values.put(COLUMN_DESCRICAO_AP, novaDescricao)

        val db = writableDatabase
        return db.update(
            TABLE_APARELHOS,
            values,
            "$COLUMN_ID_APARELHO = ?",
            arrayOf(id.toString())
        )
    }

    fun atualizarModoOperacao(modoOperacao: ModoOperacao) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_MODO_OP, modoOperacao.modoOperacao)
        values.put(COLUMN_DESCRICAO_MOD, modoOperacao.descricao)

        db.update(TABLE_MODO_OPS, values, "$COLUMN_ID_MOD = ?", arrayOf(modoOperacao.id.toString()))
        db.close()
    }

    fun getAllModoOperacoes(): List<ModoOperacao> {
        val modoOperacaoList = mutableListOf<ModoOperacao>()

        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_MODO_OPS"

        val cursor = db.rawQuery(query, null)

        try {
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_MOD))
                val descricao = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO_MOD))
                val modoOperacao = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MODO_OP))

                val modo = ModoOperacao(id, descricao, modoOperacao)
                modoOperacaoList.add(modo)
            }
        } catch (e: Exception) {
            Log.e("TAG", "Erro ao recuperar todos os modos de operação: ${e.message}")
        } finally {
            cursor.close()
        }
        return modoOperacaoList
    }

    private fun modoOperacaoExiste(db: SQLiteDatabase): Boolean {
        val query = "SELECT COUNT(*) FROM $TABLE_MODO_OPS"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            if (it.moveToFirst()) {
                val count = it.getInt(0)
                return count > 0
            }
        }
        return false
    }

    private fun aparelhoExiste(db: SQLiteDatabase?): Boolean {
        val query = "SELECT COUNT(*) FROM $TABLE_APARELHOS"
        val cursor = db?.rawQuery(query, null)

        cursor.use {
            if (it != null) {
                if (it.moveToFirst()){
                    val count = it?.getInt(0)
                    if (count != null) {
                        return count > 0
                    }
                }
            }
        }
        return false
    }

    private fun dispositivoExiste(db: SQLiteDatabase?): Boolean {
        val query = "SELECT COUNT(*) FROM $TABLE_DISPOSITIVOS"
        val cursor = db?.rawQuery(query, null)

        cursor.use {
            if (it != null) {
                if (it.moveToFirst()){
                    val count = it?.getInt(0)
                    if (count != null) {
                        return count > 0
                    }
                }
            }
        }
        return false
    }
}