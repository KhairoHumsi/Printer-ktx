package com.dantsu.escposprinter.connection.tcp

import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Create un instance of TcpConnection.
 *
 * @param address IP address of the device
 * @param port    Port of the device
 */
class TcpConnection(private val address: String, private val port: Int) : TcpDeviceConnection() {
    private var socket: Socket? = null

    /**
     * Check if the TCP device is connected by socket.
     *
     * @return true if is connected
     */

    override suspend fun isConnected(): Boolean =
        socket != null && socket!!.isConnected && super.isConnected()

    /**
     * Start socket connection with the TCP device.
     */
    @Throws(EscPosConnectionException::class)
    override suspend fun connect(): TcpConnection {
        if (this.isConnected()) {
            return this
        }
        try {
            socket = Socket()
            socket!!.connect(InetSocketAddress(InetAddress.getByName(address), port))
            stream = socket!!.getOutputStream()
            data = ByteArray(0)
        } catch (e: IOException) {
            e.printStackTrace()
            socket = null
            stream = null
            throw EscPosConnectionException("Unable to connect to TCP device.")
        }
        return this
    }

    /**
     * Close the socket connection with the TCP device.
     */
    override suspend fun disconnect(): TcpConnection {
        data = ByteArray(0)
        if (stream != null) {
            try {
                stream!!.close()
                stream = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (socket != null) {
            try {
                socket!!.close()
                socket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return this
    }
}