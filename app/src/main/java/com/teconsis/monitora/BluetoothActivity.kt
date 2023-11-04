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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class BluetoothActivity : AppCompatActivity() {

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val discoveredDevices = ArrayList<BluetoothDevice>()
    private var deviceList: ListView? = null
    private lateinit var scanButton: Button
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var messageOutput: TextView

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    discoveredDevices.add(device)
                }
                val deviceNames = discoveredDevices.map { it.name ?: "Unnamed" }.toTypedArray()
                deviceList?.adapter = ArrayAdapter(this@BluetoothActivity, android.R.layout.simple_list_item_1, deviceNames)
            }
        }
    }

    private val hc05DeviceName = "HC-05" // Nome do dispositivo HC-05
    private val hc05UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Exemplo de UUID genérico
    private var bluetoothSocket: BluetoothSocket? = null

    companion object {
        private const val REQUEST_ENABLE_BT = 1
        private const val REQUEST_BLUETOOTH_SCAN = 2
        private const val REQUEST_BLUETOOTH_CONNECT = 3 // Nova constante para solicitar permissão BLUETOOTH_CONNECT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            showBluetoothUnavailableDialog()
        } else if (!bluetoothAdapter?.isEnabled!!) {
            requestBluetoothEnable()
        }

        deviceList = findViewById(R.id.deviceList)
        scanButton = findViewById(R.id.scanButton)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        messageOutput = findViewById(R.id.messageOutput)

        val permission = Manifest.permission.BLUETOOTH_ADMIN
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_ENABLE_BT)
        }

        scanButton.setOnClickListener {
            startDiscovery()
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

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(bluetoothReceiver, filter)

        connectToHC05()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
        closeBluetoothSocket()
    }

    private fun startDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_SCAN), REQUEST_BLUETOOTH_SCAN)
            return
        }
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter!!.cancelDiscovery()
        }
        discoveredDevices.clear()
        deviceList?.adapter = null // Limpa a lista de dispositivos antes de iniciar uma nova descoberta
        bluetoothAdapter?.startDiscovery()
    }

    private fun connectToHC05() {
        val pairedDevices = bluetoothAdapter?.bondedDevices
        if (pairedDevices != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permissão BLUETOOTH_CONNECT
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_CONNECT)
            } else {
                for (device in pairedDevices) {
                    if (device.name == hc05DeviceName) {
                        try {
                            bluetoothSocket = device.createRfcommSocketToServiceRecord(hc05UUID)
                            bluetoothSocket?.connect()
                            // Agora você está conectado ao dispositivo HC-05
                            showMessage("Conectado ao dispositivo: ${device.name}")
                            // Você pode usar o bluetoothSocket para a comunicação
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showMessage("Erro ao conectar ao dispositivo: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    private fun closeBluetoothSocket() {
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showMessage(message: String) {
        messageOutput.text = messageOutput.text.toString() + "\n" + message
    }

    private fun showBluetoothUnavailableDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bluetooth Indisponível")
        builder.setMessage("O Bluetooth não está disponível neste dispositivo.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }
}
