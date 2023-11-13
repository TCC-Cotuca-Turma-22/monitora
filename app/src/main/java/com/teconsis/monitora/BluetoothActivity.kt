package com.teconsis.monitora

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val messageInput: EditText by lazy { findViewById(R.id.messageInput) }
    private val sendButton: Button by lazy { findViewById(R.id.sendButton) }
    private val receivedMessages: TextView by lazy { findViewById(R.id.receivedMessages) }
    private val connectionStatusText: TextView by lazy { findViewById(R.id.connectionStatusText) }
    private val pairedDeviceName: TextView by lazy { findViewById(R.id.pairedDeviceName) }

    private val hc05DeviceName = "HC-05"
    private val hc05UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val BLUETOOTH_PERMISSION_REQUEST = 2
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        try {
            if (bluetoothAdapter == null) {
                showErrorDialog("Bluetooth Indisponível", "O Bluetooth não está disponível neste dispositivo.")
                return
            }

            if (!bluetoothAdapter!!.isEnabled) {
                requestBluetoothEnable()
            } else {
                checkPairedAndConnectedDevices()
            }

            sendButton.setOnClickListener {
                val message = messageInput.text.toString()
                if (bluetoothSocket != null && bluetoothSocket!!.isConnected) {
                    try {
                        bluetoothSocket!!.outputStream.write(message.toByteArray())
                        messageInput.text.clear()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        showMessage("Erro ao enviar mensagem: ${e.message}")
                    }
                } else {
                    showMessage("Dispositivo não conectado.")
                    checkPairedAndConnectedDevices()  // Tente reconectar automaticamente
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro inesperado: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            closeBluetoothSocket()
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao encerrar: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    BLUETOOTH_PERMISSION_REQUEST
                )
            } else {
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao solicitar ativação do Bluetooth: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPairedAndConnectedDevices() {
        try {
            val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
            if (pairedDevices != null) {
                val iterator = pairedDevices.iterator()
                while (iterator.hasNext()) {
                    val device = iterator.next()
                    if (device.name == hc05DeviceName) {
                        showMessage("Dispositivo pareado encontrado: ${device.name}")
                        updateConnectionStatus("Conectando ao dispositivo: ${device.name}")
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(hc05UUID)
                        if (bluetoothSocket != null) {
                            if (!bluetoothSocket!!.isConnected) {
                                bluetoothSocket?.connect()
                            }
                            showMessage("Conectado ao dispositivo: ${device.name}")
                            updateConnectionStatus()
                            pairedDeviceName.text = "Dispositivo: ${device.name}"

                            // Envie senha e comando AT aqui
                            if (sendPasswordAndATCommand()) {
                                startListeningForMessages()
                            } else {
                                showMessage("Falha ao enviar senha e comando AT.")
                                closeBluetoothSocket()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao verificar dispositivos pareados: ${e.message}")
        }
    }

    private fun sendPasswordAndATCommand(): Boolean {
        return try {
            if (bluetoothSocket != null && bluetoothSocket!!.isConnected) {
                showMessage("Enviando senha para o dispositivo.")
                outputStream = bluetoothSocket!!.outputStream
                outputStream?.write("1234".toByteArray())

                // Aguarde um pouco para garantir que a senha seja processada
                Thread.sleep(500)

                // Envie o comando AT
                outputStream?.write("AT+MODE=1".toByteArray())

                // Leia a resposta do dispositivo
                val response = readResponse()
                showMessage("Resposta do dispositivo: $response")

                // Verifique se o dispositivo respondeu corretamente
                response.contains("OK", ignoreCase = true)
            } else {
                showMessage("Dispositivo não conectado.")
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showMessage("Erro ao enviar mensagem: ${e.message}")
            false
        }
    }

    private fun readResponse(): String {
        val buffer = ByteArray(1024)
        val bytesRead = inputStream?.read(buffer) ?: -1
        return String(buffer, 0, bytesRead)
    }

    private fun startListeningForMessages() {
        // Implementar a escuta de mensagens aqui, se necessário
    }

    private fun closeBluetoothSocket() {
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao encerrar conexão Bluetooth: ${e.message}")
        }
    }

    private fun showErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            receivedMessages.text = receivedMessages.text.toString() + "\n" + message
        }
    }

    private fun updateConnectionStatus(status: String = "Status: Conectado") {
        runOnUiThread {
            connectionStatusText.text = status
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão concedida, tentar conectar novamente
                    checkPairedAndConnectedDevices()
                } else {
                    showMessage("Permissão Bluetooth negada. Não é possível conectar.")
                }
            }
        }
    }
}
