package com.khairo.escposprinter.connection.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.khairo.escposprinter.connection.DeviceConnection
import com.khairo.escposprinter.exceptions.EscPosConnectionException
import java.io.IOException
import java.util.*

class BluetoothConnection
/**
 * Create un instance of BluetoothConnection.
 *
 * @param device an instance of BluetoothDevice
 */(
    /**
     * Get the instance BluetoothDevice connected.
     *
     * @return an instance of BluetoothDevice
     */
    val device: BluetoothDevice?
) : DeviceConnection() {
    private var socket: BluetoothSocket? = null

    /**
     * Check if OutputStream is open.
     *
     * @return true if is connected
     */
    override fun isConnected(): Boolean {
        return socket != null && socket!!.isConnected && super.isConnected()
    }

    /**
     * Start socket connection with the bluetooth device.
     */
    @SuppressLint("MissingPermission")
    @Throws(EscPosConnectionException::class)
    override fun connect(): BluetoothConnection {
        if (isConnected()) {
            return this
        }
        if (device == null) {
            throw EscPosConnectionException("Bluetooth device is not connected.")
        }
        val uuids = device.uuids
        val uuid = if (uuids != null && uuids.isNotEmpty()) uuids[0].uuid else UUID.randomUUID()
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid)
            socket!!.connect()
            stream = socket!!.outputStream
            data = ByteArray(0)
        } catch (e: IOException) {
            e.printStackTrace()
            socket = null
            stream = null
            throw EscPosConnectionException("Unable to connect to bluetooth device.")
        }
        return this
    }

    /**
     * Close the socket connection with the bluetooth device.
     */
    override fun disconnect(): BluetoothConnection {
        data = ByteArray(0)
        if (stream != null) {
            try {
                stream!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            stream = null
        }
        if (socket != null) {
            try {
                socket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            socket = null
        }
        return this
    }
}