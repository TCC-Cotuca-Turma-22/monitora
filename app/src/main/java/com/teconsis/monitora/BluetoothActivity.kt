package com.teconsis.monitora

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.UUID

class BluetoothActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val messageInput: EditText by lazy { findViewById(R.id.messageInput) }
    private val sendButton: Button by lazy { findViewById(R.id.sendButton) }
    private val messageOutput: TextView by lazy { findViewById(R.id.messageOutput) }
    private val connectionStatusIcon: ImageView by lazy { findViewById(R.id.connectionStatusIcon) }
    private val connectionStatusText: TextView by lazy { findViewById(R.id.connectionStatusText) }
    private val backButton: Button by lazy { findViewById(R.id.getBackButton) }
    private val pairedDeviceName: TextView by lazy { findViewById(R.id.pairedDeviceName) }

    private val hc05DeviceName = "HC-05"
    private val hc05UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
    private var bluetoothSocket: BluetoothSocket? = null

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_BLUETOOTH_SCAN = 2
        private const val REQUEST_BLUETOOTH_CONNECT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        try {
            if (bluetoothAdapter == null) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Bluetooth Indisponível")
                builder.setMessage("O Bluetooth não está disponível neste dispositivo.")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                if (!bluetoothAdapter!!.isEnabled) {
                    requestBluetoothEnable()
                }

                backButton.setOnClickListener {
                    finish()
                }

                sendButton.setOnClickListener {
                    val message = messageInput.text.toString()
                    if (bluetoothSocket != null) {
                        try {
                            bluetoothSocket!!.outputStream.write(message.toByteArray())
                            messageInput.text.clear()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            showMessage("Erro ao enviar mensagem: ${e.message}")
                        }
                    } else {
                        showMessage("Dispositivo não conectado.")
                    }
                }

                connectToHC05()
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

    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_ENABLE_BT)
            } else {
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao solicitar ativação do Bluetooth: ${e.message}")
        }
    }

    private fun connectToHC05() {
        try {
            val pairedDevices = bluetoothAdapter?.bondedDevices
            if (pairedDevices != null) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_CONNECT)
                } else {
                    for (device in pairedDevices) {
                        if (device.name == hc05DeviceName) {
                            try {
                                bluetoothSocket = device.createRfcommSocketToServiceRecord(hc05UUID)
                                bluetoothSocket?.connect()
                                showMessage("Conectado ao dispositivo: ${device.name}")
                                pairedDeviceName.text = "Conectado a: ${device.name}"
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showMessage("Erro ao conectar ao dispositivo: ${e.message}")
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao conectar ao dispositivo HC-05: ${e.message}")
        }
    }

    private fun closeBluetoothSocket() {
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage("Erro ao encerrar conexão Bluetooth: ${e.message}")
        }
    }

    private fun showMessage(message: String) {
        messageOutput.text = messageOutput.text.toString() + "\n" + message
    }
}
